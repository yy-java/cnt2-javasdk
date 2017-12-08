package com.yy.cnt2.client;

import io.grpc.ManagedChannel;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import com.yy.cnt2.grpc.api.ConfigCenterServiceGrpc;
import com.yy.cnt2.grpc.api.QueryRequest;
import com.yy.cnt2.grpc.api.QueryResponse;
import com.yy.cnt2.grpc.api.RegisterRequest;
import com.yy.cnt2.grpc.api.RegisterResponse;
import com.yy.cnt2.grpc.api.ResponseMessage;
import com.yy.cnt2.grpc.api.ValueChangeResultRequest;
import com.yy.cnt2.grpc.api.ValueChangeResultResponse;
import com.yy.cnt2.grpc.api.ValueChangeResultRequest.ValueChangeResult;
import com.yy.cnt2.util.NetWorkUtils;
import com.yy.cnt2.util.ProgressUtil;

/**
 * ConfigCenterClient
 *
 * @author xlg
 * @since 2017/7/7
 */
public class ConfigCenterClient implements Closeable {
    private static final int SUCCESS_CODE = 1;

    private ConfigCenterServiceGrpc.ConfigCenterServiceFutureStub stub;
    private ManagedChannel channel;

    public ConfigCenterClient(ManagedChannel channel) {
        this.channel = channel;
        this.stub = ConfigCenterServiceGrpc.newFutureStub(channel);
    }

    /**
     * 启动的时候注册服务:获取服务id
     *
     * @param appName 应用名
     * @param profile 应用Profile
     * @return 节点Id
     */
    public String registerClient(String appName, String profile) {
        int pid = ProgressUtil.getPid();
        String ip = NetWorkUtils.getAppServerIp();

        com.yy.cnt2.grpc.api.RegisterRequest.Builder request = com.yy.cnt2.grpc.api.RegisterRequest.newBuilder();
        request.setApp(appName).setProfile(profile).setServerIp(ip).setPid(String.valueOf(pid));

        RegisterResponse response = getResult(this.stub.registerClient(request.build()));

        int result = response.getResult();
        if (SUCCESS_CODE != result) {
            throw new RuntimeException("Register failed! Code:" + result);
        }

        return response.getNodeId();
    }

    /**
     * 根据appId，profile 查询所有配置
     *
     * @param appName 应用名
     * @param profile 应用Profile
     * @return Map, Key=>配置信息
     */
    public Map<String, ResponseMessage> queryAll(String appName, String profile) {
        QueryRequest.Builder request = QueryRequest.newBuilder().setApp(appName).setProfile(profile);
        QueryResponse response = getResult(this.stub.queryAll(request.build()));

        if (null == response) {
            Collections.emptyMap();
        }

        Map<String, ResponseMessage> map = Maps.newHashMap();
        List<ResponseMessage> list = response.getResultList();

        for (ResponseMessage message : list) {
            map.put(message.getKey(), message);
        }

        return map;
    }

    /**
     * 根据appId，profile，key，keyV}rsion查询，keyVersion=0时，返回最新数据，否则返回指定版本数据
     *
     * @param appName 应用名
     * @param profile 应用Profile
     * @param key     配置名
     * @param version 配置版本号
     * @return 配置信息
     */
    public ResponseMessage queryKey(String appName, String profile, String key, long version) {
        QueryRequest.Builder request = QueryRequest.newBuilder().setApp(appName).setProfile(profile)
                .setKeyVersion(version).setKey(key);
        ResponseMessage response = getResult(this.stub.queryKey(request.build()));

        return response;
    }

    /**
     * 发布处理结果通知
     *
     * @param nodeId       节点id
     * @param appName      应用名
     * @param profile      应用Profile
     * @param key          配置名
     * @param deployId     配置部署Id
     * @param version      配置版本号
     * @param notifyStatus 配置更新结果
     * @return 调用成功返回true,否则返回false
     */
    public boolean valueChangeResultNotify(String nodeId, String appName, String profile, String key, String deployId,
            long version, boolean notifyStatus) {
        ValueChangeResultRequest.Builder request = ValueChangeResultRequest.newBuilder().setNodeId(nodeId)
                .setApp(appName).setProfile(profile).setKey(key).setDeployId(deployId).setVersion(version)
                .setResult(notifyStatus ? ValueChangeResult.SUCCESS : ValueChangeResult.FAILED);
        ValueChangeResultResponse response = getResult(this.stub.valueChangeResultNotify(request.build()));

        if (null == response) {
            return false;
        }

        return response.getStatus() == SUCCESS_CODE ? true : false;

    }

    private <T> T getResult(ListenableFuture<T> future) {
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Get result failed!", e);
        }
    }

    @Override
    public void close() throws IOException {
        if (null != this.channel) {
            this.channel.shutdownNow();
        }
    }
}
