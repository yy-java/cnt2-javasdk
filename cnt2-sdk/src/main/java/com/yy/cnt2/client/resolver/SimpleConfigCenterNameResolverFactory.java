package com.yy.cnt2.client.resolver;

import com.google.common.base.Preconditions;
import io.grpc.Attributes;
import io.grpc.NameResolver;
import io.grpc.internal.GrpcUtil;

import java.net.URI;
import java.util.List;

/**
 * Copy from {@link com.coreos.jetcd.resolver.SimpleEtcdNameResolverFactory}
 *
 * @author xlg
 * @since 2017/7/8
 */
public class SimpleConfigCenterNameResolverFactory extends NameResolver.Factory {
    private static final String SCHEME = "cnt2";

    private final List<URI> uris;

    public SimpleConfigCenterNameResolverFactory(List<URI> uris) {
        this.uris = uris;
    }

    @Override
    public NameResolver newNameResolver(URI targetUri, Attributes params) {
        if (SCHEME.equals(targetUri.getScheme())) {
            String targetPath = Preconditions.checkNotNull(targetUri.getPath(), "targetPath");
            Preconditions.checkArgument(targetPath.startsWith("/"),
                    "the path component (%s) of the target (%s) must start with '/'", targetPath, targetUri);
            String name = targetPath.substring(1);
            return new SimpleConfigCenterNameResolver(name, GrpcUtil.SHARED_CHANNEL_EXECUTOR, this.uris);
        } else {
            return null;
        }
    }

    @Override
    public String getDefaultScheme() {
        return SCHEME;
    }

}
