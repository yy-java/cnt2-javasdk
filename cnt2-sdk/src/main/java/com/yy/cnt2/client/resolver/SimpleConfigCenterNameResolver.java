package com.yy.cnt2.client.resolver;

import com.google.common.collect.Lists;
import io.grpc.EquivalentAddressGroup;
import io.grpc.internal.SharedResourceHolder;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Copy from {@link com.coreos.jetcd.resolver.SimpleEtcdNameResolver}
 *
 * @author xlg
 * @since 2017/7/8
 */
public class SimpleConfigCenterNameResolver extends AbstractConfigCenterNameResolver {

    private final List<SocketAddress> servers;

    public SimpleConfigCenterNameResolver(String name, SharedResourceHolder.Resource<ExecutorService> executorResource,
            List<URI> uris) {
        super(name, executorResource);

        List<SocketAddress> list = Lists.newArrayList();
        for (URI uri : uris) {
            list.add(new InetSocketAddress(uri.getHost(), uri.getPort()));
        }
        this.servers = Collections.unmodifiableList(list);
    }

    @Override
    protected List<SocketAddress> getServers() {
        return servers;
    }

    @Override
    protected List<EquivalentAddressGroup> getEquivalentAddressGroups() {
        List<SocketAddress> serverList = getServers();
        List<EquivalentAddressGroup> list = Lists.newLinkedList();

        for (SocketAddress socketAddress : serverList) {
            list.add(new EquivalentAddressGroup(socketAddress));
        }

        return Collections.unmodifiableList(list);
    }
}
