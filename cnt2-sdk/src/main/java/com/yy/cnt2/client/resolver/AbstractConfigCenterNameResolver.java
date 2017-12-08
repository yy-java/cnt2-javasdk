package com.yy.cnt2.client.resolver;

import com.google.common.base.Preconditions;
import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.internal.SharedResourceHolder;

import javax.annotation.concurrent.GuardedBy;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.util.Collections.singletonList;

/**
 * Copy form {@link com.coreos.jetcd.resolver.AbstractEtcdNameResolver}
 *
 * @author xlg
 * @since 2017/7/8
 */
public abstract class AbstractConfigCenterNameResolver extends NameResolver {
    private final String authority;
    private final SharedResourceHolder.Resource<ExecutorService> executorResource;
    private final Runnable resolutionRunnable;

    @GuardedBy("this")
    protected boolean shutdown;
    @GuardedBy("this")
    private boolean resolving;
    @GuardedBy("this")
    private Listener listener;
    @GuardedBy("this")
    private ExecutorService executor;

    public AbstractConfigCenterNameResolver(String name,
            SharedResourceHolder.Resource<ExecutorService> executorResource) {
        URI nameUri = URI.create("//" + name);

        this.executorResource = executorResource;
        this.authority = Preconditions
                .checkNotNull(nameUri.getAuthority(), "nameUri (%s) doesn't have an authority", nameUri);
        this.resolutionRunnable = new AbstractConfigCenterNameResolver.ResolverTask();
    }

    @Override
    public String getServiceAuthority() {
        return authority;
    }

    @Override
    public final synchronized void start(Listener listener) {
        Preconditions.checkState(this.listener == null, "already started");
        this.executor = SharedResourceHolder.get(executorResource);
        this.listener = Preconditions.checkNotNull(listener, "listener");
        resolve();
    }

    @Override
    public final synchronized void refresh() {
        Preconditions.checkState(listener != null, "not started");
        resolve();
    }

    @Override
    public final synchronized void shutdown() {
        if (shutdown) {
            return;
        }
        shutdown = true;
        if (executor != null) {
            executor = SharedResourceHolder.release(executorResource, executor);
        }
    }

    @GuardedBy("this")
    private void resolve() {
        if (resolving || shutdown) {
            return;
        }
        executor.execute(resolutionRunnable);
    }

    protected abstract List<SocketAddress> getServers();

    /**
     * Get EquivalentAddressGroup List,default list only contains one EquivalentAddressGroup which servers from
     * {@link AbstractConfigCenterNameResolver#getServers()},override me if needed!
     *
     * @return
     */
    protected List<EquivalentAddressGroup> getEquivalentAddressGroups() {
        return singletonList(new EquivalentAddressGroup(getServers()));
    }

    protected Attributes getAttributes() {
        return Attributes.EMPTY;
    }

    /**
     * Helper task to resolve servers.
     */
    private final class ResolverTask implements Runnable {

        @Override
        public void run() {
            Listener savedListener;
            synchronized (AbstractConfigCenterNameResolver.this) {
                if (shutdown) {
                    return;
                }
                resolving = true;
                savedListener = listener;
            }

            try {
                savedListener.onAddresses(getEquivalentAddressGroups(), getAttributes());
            } finally {
                synchronized (AbstractConfigCenterNameResolver.this) {
                    resolving = false;
                }
            }
        }
    }
}
