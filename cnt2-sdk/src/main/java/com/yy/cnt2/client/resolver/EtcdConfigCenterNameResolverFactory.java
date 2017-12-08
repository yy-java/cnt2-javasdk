package com.yy.cnt2.client.resolver;

import com.coreos.jetcd.Client;
import com.google.common.base.Preconditions;
import io.grpc.Attributes;
import io.grpc.NameResolver;
import io.grpc.internal.GrpcUtil;

import javax.annotation.Nullable;
import java.net.URI;

/**
 * EtcdConfigCenterNameResolverFactory
 * 
 * @author xlg
 * @since 2017/7/11
 */
public class EtcdConfigCenterNameResolverFactory extends NameResolver.Factory {
    private static final String SCHEME = "cnt2";

    private Client etcdClient;

    public EtcdConfigCenterNameResolverFactory(Client etcdClient) {
        this.etcdClient = etcdClient;
    }

    @Nullable
    @Override
    public NameResolver newNameResolver(URI targetUri, Attributes params) {
        if (SCHEME.equals(targetUri.getScheme())) {
            String targetPath = Preconditions.checkNotNull(targetUri.getPath(), "targetPath");
            Preconditions.checkArgument(targetPath.startsWith("/"),
                    "the path component (%s) of the target (%s) must start with '/'", targetPath, targetUri);
            String name = targetPath.substring(1);
            return new EtcdConfigCenterNameResolver(name, GrpcUtil.SHARED_CHANNEL_EXECUTOR, this.etcdClient);
        } else {
            return null;
        }

    }

    @Override
    public String getDefaultScheme() {
        return SCHEME;
    }
}
