package com.yy.cnt2.server;

import com.google.common.collect.Maps;
import com.yy.cnt2.grpc.api.*;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xlg
 * @since 2017/7/10
 */
public class ConfigCenterTestServer extends ConfigCenterServiceGrpc.ConfigCenterServiceImplBase {
    static AtomicInteger atomicInteger = new AtomicInteger(1);
    int port;
    public static String[] profiles = { "production", "development" };
    public static Map<String, Map<String, ResponseMessage>> configs = Maps.newHashMap();

    static {
        for (String profile : profiles) {
            Map<String, ResponseMessage> config = Maps.newConcurrentMap();

            for (int i = 1; i < 6; i++) {
                ResponseMessage.Builder message = ResponseMessage.newBuilder();
                message.setKey("configKey" + i);
                message.setProfile(profile);
                message.setPath("/testApp/profiles/" + profile + "/" + message.getKey());
                message.setValue("configValue" + i);
                message.setVersion(atomicInteger.incrementAndGet());

                config.put(message.getKey(), message.build());
            }

            configs.put(profile, config);
        }
    }

    public ConfigCenterTestServer(int port) {
        this.port = port;
    }

    @Override
    public void queryAll(QueryRequest request, StreamObserver<QueryResponse> responseObserver) {
        System.out.println("Port:" + port + " Receive: " + request);
        String app = request.getApp();
        String profile = request.getProfile();

        QueryResponse.Builder response = QueryResponse.newBuilder();
        response.addAllResult(configs.get(profile).values());

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void queryKey(QueryRequest request, StreamObserver<ResponseMessage> responseObserver) {
        System.out.println("Port:" + port + " Receive: " + request);
        String key = request.getKey();
        String app = request.getApp();
        String profile = request.getProfile();
        long keyVersion = request.getKeyVersion();

        Map<String, ResponseMessage> map = configs.get(profile);
        ResponseMessage responseMessage = map.get(key);
        ResponseMessage message = ResponseMessage.newBuilder(responseMessage).setVersion(responseMessage.getVersion())
                .setValue(responseMessage.getValue() + "_" + responseMessage.getVersion()).build();

        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    @Override
    public void valueChangeResultNotify(ValueChangeResultRequest request,
            StreamObserver<ValueChangeResultResponse> responseObserver) {
        System.out.println("Port:" + port + " Receive: " + request);

        ValueChangeResultResponse.Builder builder = ValueChangeResultResponse.newBuilder().setStatus(1).setMsg("ok");
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void registerClient(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
        System.out.println("Port:" + port + " Receive: " + request);
        RegisterResponse.Builder builder = RegisterResponse.newBuilder();
        builder.setNodeId("123456789");

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    private Server server;

    public static void startServer(int... ports) {
        final CountDownLatch waiter = new CountDownLatch(ports.length);

        for (final int port : ports) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        ConfigCenterTestServer server = new ConfigCenterTestServer(port);
                        server.start(port);
                        waiter.countDown();
                        server.blockUntilShutdown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        try {
            waiter.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Object lock = new Object();
        try {
            new ConfigCenterTestServer(8888).start(8888);
        } catch (IOException e) {
            new ConfigCenterTestServer(9999).start(9999);
        }

        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void start(int port) throws IOException {
        /* The port on which the server should run */
        server = ServerBuilder.forPort(port).addService(new ConfigCenterTestServer(port)).build().start();
        System.out.println("Server started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                ConfigCenterTestServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}
