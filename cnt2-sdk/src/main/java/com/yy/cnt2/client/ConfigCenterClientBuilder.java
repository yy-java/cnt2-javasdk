package com.yy.cnt2.client;

import com.coreos.jetcd.Client;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.yy.cnt2.client.lb.SameGroupPriorityLoadBalancerFactory;
import com.yy.cnt2.client.resolver.EtcdConfigCenterNameResolverFactory;
import com.yy.cnt2.client.resolver.SimpleConfigCenterNameResolverFactory;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolver;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

/**
 * ConfigCenterClient builder
 *
 * @author xlg
 * @since 2017/7/8
 */
public class ConfigCenterClientBuilder {
    private List<String> endpointList = Lists.newArrayList();
    private NameResolver.Factory nameResolverFactory;
    private Client etcdClient;
    public static final String SVR_NAME = "cnt2";

    public static ConfigCenterClientBuilder newBuilder() {
        return new ConfigCenterClientBuilder();
    }

    public ConfigCenterClientBuilder setEtcdClient(Client etcdClient) {
        this.etcdClient = etcdClient;
        return this;
    }

    public ConfigCenterClientBuilder setEndpoints(String... endpoints) {
        if (null != endpoints) {
            for (String endpoint : endpoints) {
                endpointList.add(endpoint);
            }
        }
        return this;
    }

    public ConfigCenterClientBuilder setNameResolverFactory(NameResolver.Factory nameResolverFactory) {
        this.nameResolverFactory = nameResolverFactory;
        return this;
    }

    public ConfigCenterClientBuilder addEndpoints(String endpoint) {
        this.endpointList.add(endpoint);
        return this;
    }

    public ConfigCenterClient build() {
        NameResolver.Factory factory = null;
        if (null != this.nameResolverFactory) {
            factory = this.nameResolverFactory;
        } else if (!endpointList.isEmpty()) {
            factory = new SimpleConfigCenterNameResolverFactory(
                    Lists.transform(this.endpointList, new Function<String, URI>() {
                        @Override
                        public URI apply(String input) {
                            try {
                                return new URI(!input.startsWith("http://") ? "http://" + input : input);
                            } catch (URISyntaxException e) {
                                throw new IllegalArgumentException(e);
                            }
                        }
                    }));
        } else if (null != etcdClient) {
            factory = new EtcdConfigCenterNameResolverFactory(etcdClient);
        }

        checkState(null != factory, "Init NameResolver.Factory failed!!");

        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forTarget(SVR_NAME)
                .loadBalancerFactory(SameGroupPriorityLoadBalancerFactory.getInstance()).nameResolverFactory(factory)
                .usePlaintext(true);

        return new ConfigCenterClient(channelBuilder.build());
    }
}
