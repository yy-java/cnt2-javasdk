package com.yy.cnt2.client;

import com.yy.cs.base.hostinfo.NetType;

import java.util.Map;

import static io.grpc.Attributes.Key;

/**
 * @author xlg
 * @since 2017/7/17
 */
public interface Consts {
    Key<Map<String, NetType>> IP_NET_TYPE_MAP_KEY = Key.of("ip_net_type_map");
    Key<Integer> SERVICE_GROUP_ID_KEY = Key.of("service_group_id");
    Key<NetType> IP_NET_TYPE_KEY = Key.of("ip_net_type");
}
