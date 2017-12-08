package com.yy.cnt2.client.lb;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yy.cnt2.domain.HostInfo;
import com.yy.cnt2.util.NetType;
import io.grpc.*;
import io.grpc.LoadBalancer.SubchannelPicker;

import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.yy.cnt2.client.Consts.IP_NET_TYPE_MAP_KEY;
import static com.yy.cnt2.client.Consts.SERVICE_GROUP_ID_KEY;
import static io.grpc.ConnectivityState.*;

/**
 * 优先使用同组的服务，同组服务之间使用RoundRobin算法
 * <p>
 * Copy from {@link io.grpc.util.RoundRobinLoadBalancerFactory.RoundRobinLoadBalancer}
 *
 * @author xlg
 * @since 2017/7/11
 */
public class SameGroupPriorityLoadBalancerFactory extends LoadBalancer.Factory {

    private static final SameGroupPriorityLoadBalancerFactory INSTANCE = new SameGroupPriorityLoadBalancerFactory();

    private SameGroupPriorityLoadBalancerFactory() {
    }

    public static SameGroupPriorityLoadBalancerFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public LoadBalancer newLoadBalancer(LoadBalancer.Helper helper) {
        return new SameGroupPriorityLoadBalancer(helper);
    }

    /**
     * Copy from {@link io.grpc.util.RoundRobinLoadBalancerFactory.RoundRobinLoadBalancer}
     */
    public static class SameGroupPriorityLoadBalancer extends LoadBalancer {
        private final Helper helper;
        private final Map<EquivalentAddressGroup, Subchannel> subchannels = new HashMap<>();

        @VisibleForTesting
        static final Attributes.Key<AtomicReference<ConnectivityStateInfo>> STATE_INFO = Attributes.Key
                .of("state-info");

        SameGroupPriorityLoadBalancer(Helper helper) {
            this.helper = checkNotNull(helper, "helper");
        }

        //让每个EquivalentAddressGroup只包含一个ip
        private List<EquivalentAddressGroup> processEquivalentAddressGroup(List<EquivalentAddressGroup> groups) {
            List<EquivalentAddressGroup> list = Lists.newLinkedList();

            for (EquivalentAddressGroup group : groups) {
                List<SocketAddress> addresses = group.getAddresses();
                Attributes attributes = group.getAttributes();

                Map<String, NetType> netTypeMap = attributes.get(IP_NET_TYPE_MAP_KEY);
                Integer groupId = attributes.get(SERVICE_GROUP_ID_KEY);
                //优先使用电信网
                SocketAddress address = pickAddressByNetTypeOrPickFirstIfNotFound(addresses, netTypeMap, NetType.CTL);
                if (null == address) {
                    continue;
                }
                EquivalentAddressGroup addressGroup = new EquivalentAddressGroup(address,
                        Attributes.newBuilder().set(SERVICE_GROUP_ID_KEY, groupId).build());

                list.add(addressGroup);
            }

            return list;
        }

        private SocketAddress pickAddressByNetTypeOrPickFirstIfNotFound(List<SocketAddress> addresses,
                Map<String, NetType> netTypeMap, NetType targetNetType) {

            for (SocketAddress address : addresses) {
                if (address instanceof InetSocketAddress) {
                    String hostName = ((InetSocketAddress) address).getHostName();
                    NetType netType = null == netTypeMap ? null : netTypeMap.get(hostName);
                    if (targetNetType == netType) {
                        return address;
                    }
                }
            }

            return addresses.size() > 0 ? addresses.get(0) : null;
        }

        @Override
        public void handleResolvedAddressGroups(List<EquivalentAddressGroup> servers, Attributes attributes) {
            servers = processEquivalentAddressGroup(servers);

            Set<EquivalentAddressGroup> currentAddrs = subchannels.keySet();
            Set<EquivalentAddressGroup> latestAddrs = toSet(servers);
            Set<EquivalentAddressGroup> addedAddrs = setsDifference(latestAddrs, currentAddrs);
            Set<EquivalentAddressGroup> removedAddrs = setsDifference(currentAddrs, latestAddrs);

            // Create new subchannels for new addresses.
            for (EquivalentAddressGroup addressGroup : addedAddrs) {
                // NB(lukaszx0): we don't merge `attributes` with `subchannelAttr` because subchannel
                // doesn't need them. They're describing the resolved server list but we're not taking
                // any action based on this information.
                Attributes subchannelAttrs = Attributes.newBuilder()
                        // NB(lukaszx0): because attributes are immutable we can't set new value for the key
                        // after creation but since we can mutate the values we leverge that and set
                        // AtomicReference which will allow mutating state info for given channel.
                        .set(STATE_INFO,
                                new AtomicReference<ConnectivityStateInfo>(ConnectivityStateInfo.forNonError(IDLE)))
                        .setAll(addressGroup.getAttributes()).build();

                Subchannel subchannel = checkNotNull(helper.createSubchannel(addressGroup, subchannelAttrs),
                        "subchannel");
                subchannels.put(addressGroup, subchannel);
                subchannel.requestConnection();
            }

            // Shutdown subchannels for removed addresses.
            for (EquivalentAddressGroup addressGroup : removedAddrs) {
                Subchannel subchannel = subchannels.remove(addressGroup);
                subchannel.shutdown();
            }

            updatePicker(getAggregatedError());
        }

        @Override
        public void handleNameResolutionError(Status error) {
            updatePicker(error);
        }

        @Override
        public void handleSubchannelState(Subchannel subchannel, ConnectivityStateInfo stateInfo) {
            if (!subchannels.containsValue(subchannel)) {
                return;
            }
            if (stateInfo.getState() == IDLE) {
                subchannel.requestConnection();
            }
            getSubchannelStateInfoRef(subchannel).set(stateInfo);
            updatePicker(getAggregatedError());
        }

        @Override
        public void shutdown() {
            for (Subchannel subchannel : getSubchannels()) {
                subchannel.shutdown();
            }
        }

        /**
         * Updates picker with the list of active subchannels (state == READY).
         */
        private void updatePicker(@Nullable Status error) {
            List<Subchannel> activeList = filterNonFailingSubchannels(getSubchannels());
            String currentGroupId = HostInfo.get().getPriGroupId();

            List<Subchannel> sameGroupList = Lists.newLinkedList();//同组
            for (Subchannel subchannel : activeList) {
                Integer groupId = subchannel.getAttributes().get(SERVICE_GROUP_ID_KEY);
                if (null != groupId && groupId.toString().equals(currentGroupId)) {
                    sameGroupList.add(subchannel);
                }
            }

            if (sameGroupList.size() > 0) {
                helper.updatePicker(new Picker(sameGroupList, error));
            } else {
                helper.updatePicker(new Picker(activeList, error));
            }
        }

        /**
         * Filters out non-ready subchannels.
         */
        private static List<Subchannel> filterNonFailingSubchannels(Collection<Subchannel> subchannels) {
            List<Subchannel> readySubchannels = new ArrayList<Subchannel>(subchannels.size());
            for (Subchannel subchannel : subchannels) {
                if (getSubchannelStateInfoRef(subchannel).get().getState() == READY) {
                    readySubchannels.add(subchannel);
                }
            }
            return readySubchannels;
        }

        /**
         * Converts list of {@link EquivalentAddressGroup} to {@link EquivalentAddressGroup} set
         */
        private static Set<EquivalentAddressGroup> toSet(List<EquivalentAddressGroup> groupList) {
            return Sets.newHashSet(groupList);
        }

        /**
         * If all subchannels are TRANSIENT_FAILURE, return the Status associated with an arbitrary
         * subchannel otherwise, return null.
         */
        @Nullable
        private Status getAggregatedError() {
            Status status = null;
            for (Subchannel subchannel : getSubchannels()) {
                ConnectivityStateInfo stateInfo = getSubchannelStateInfoRef(subchannel).get();
                if (stateInfo.getState() != TRANSIENT_FAILURE) {
                    return null;
                }
                status = stateInfo.getStatus();
            }
            return status;
        }

        @VisibleForTesting
        Collection<Subchannel> getSubchannels() {
            return subchannels.values();
        }

        private static AtomicReference<ConnectivityStateInfo> getSubchannelStateInfoRef(Subchannel subchannel) {
            return checkNotNull(subchannel.getAttributes().get(STATE_INFO), "STATE_INFO");
        }

        private static <T> Set<T> setsDifference(Set<T> a, Set<T> b) {
            Set<T> aCopy = new HashSet<T>(a);
            aCopy.removeAll(b);
            return aCopy;
        }
    }

    @VisibleForTesting
    static final class Picker extends SubchannelPicker {
        @Nullable
        private final Status status;
        private final List<LoadBalancer.Subchannel> list;
        private final int size;
        @GuardedBy("this")
        private int index = 0;

        Picker(List<LoadBalancer.Subchannel> list, @Nullable Status status) {
            this.list = Collections.unmodifiableList(list);
            this.size = list.size();
            this.status = status;
        }

        @Override
        public LoadBalancer.PickResult pickSubchannel(LoadBalancer.PickSubchannelArgs args) {
            if (size > 0) {
                return LoadBalancer.PickResult.withSubchannel(nextSubchannel());
            }

            if (status != null) {
                return LoadBalancer.PickResult.withError(status);
            }

            return LoadBalancer.PickResult.withNoResult();
        }

        private LoadBalancer.Subchannel nextSubchannel() {
            if (size == 0) {
                throw new NoSuchElementException();
            }

            synchronized (this) {
                LoadBalancer.Subchannel val = list.get(index);

                index++;
                if (index >= size) {
                    index = 0;
                }
                return val;
            }
        }

        @VisibleForTesting
        List<LoadBalancer.Subchannel> getList() {
            return list;
        }

        @VisibleForTesting
        Status getStatus() {
            return status;
        }
    }

}
