package com.yy.cnt.recipes.db.serivce;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yy.cnt.api.IControlCenterService;
import com.yy.cnt.recipes.db.common.AbstractDataSourceFactory;
import org.apache.commons.lang3.StringUtils;

public class PublicConfigService {
    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");
    private static final ConcurrentHashMap<IControlCenterService, PublicConfigService> Instances = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CopyOnWriteArraySet<AbstractDataSourceFactory<?, ?>>> follows = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, PublicConfigChangeCallback> callbacks = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<AbstractDataSourceFactory<?, ?>, Set<String>> relativeKeys = new ConcurrentHashMap<>();

    private IControlCenterService cntService;
    private ReentrantLock registryLock = new ReentrantLock();

    private PublicConfigService(IControlCenterService cntService) {
        this.cntService = cntService;
    }

    public static PublicConfigService getService(IControlCenterService cntService) {
        if (null == cntService) {
            return null;
        }
        PublicConfigService service = Instances.get(cntService);
        if (null == service) {
            service = new PublicConfigService(cntService);
            PublicConfigService oldService = Instances.putIfAbsent(cntService, service);
            if (null != oldService) {
                service = oldService;
            }
        }

        return service;
    }

    public Set<AbstractDataSourceFactory<?, ?>> getFollows(String key) {
        Set<AbstractDataSourceFactory<?, ?>> ret = new HashSet<>();
        Set<AbstractDataSourceFactory<?, ?>> set = follows.get(key);
        if (null != set) {
            ret.addAll(set);
        }
        return ret;
    }

    public void registry(String key, AbstractDataSourceFactory<?, ?> datasource) {
        registryLock.lock();
        try {
            CopyOnWriteArraySet<AbstractDataSourceFactory<?, ?>> set = null;
            if (follows.containsKey(key)) {
                set = follows.get(key);
            } else {
                set = new CopyOnWriteArraySet<>();
                callbacks.put(key, new PublicConfigChangeCallback(this, key));
                cntService.registerEventHandler(callbacks.get(key));
                follows.put(key, set);
            }
            set.add(datasource);
        } finally {
            registryLock.unlock();
        }
    }

    public void unregistry(String key, AbstractDataSourceFactory<?, ?> datasource) {
        registryLock.lock();
        try {
            CopyOnWriteArraySet<AbstractDataSourceFactory<?, ?>> set = follows.get(key);
            if (null != set) {
                set.remove(datasource);
                if (set.isEmpty()) {
                    follows.remove(key);
                    callbacks.remove(key);
                }
            }
        } finally {
            registryLock.unlock();
        }
    }

    public String parsePublicConfig(String data, AbstractDataSourceFactory<?, ?> datasource) {
        StringBuilder output = new StringBuilder();
        Matcher tokenMatcher = TOKEN_PATTERN.matcher(data);
        Set<String> oldRelatvies = relativeKeys.get(datasource);
        if (null == oldRelatvies) {
            relativeKeys.putIfAbsent(datasource, new HashSet<String>());
            oldRelatvies = relativeKeys.get(datasource);
        }
        Set<String> newRelatvies = new HashSet<>();
        int cursor = 0;
        while (tokenMatcher.find()) {
            // A token is defined as a sequence of the format "${...}".
            // A key is defined as the content between the brackets.
            int tokenStart = tokenMatcher.start();
            int tokenEnd = tokenMatcher.end();
            int keyStart = tokenMatcher.start(1);
            int keyEnd = tokenMatcher.end(1);

            output.append(data.substring(cursor, tokenStart));

            String token = data.substring(tokenStart, tokenEnd);
            String key = data.substring(keyStart, keyEnd);
            String value = cntService.getValue(key);

            this.registry(key, datasource);
            newRelatvies.add(key);

            if (StringUtils.isNotEmpty(value)) {
                output.append(value);
            } else {
                output.append(token);
            }

            cursor = tokenEnd;
        }

        for (String key : oldRelatvies) {
            if (!newRelatvies.contains(key)) {
                this.unregistry(key, datasource);
            }
        }
        relativeKeys.put(datasource, newRelatvies);

        output.append(data.substring(cursor));
        return output.toString();
    }

    public Map<String, String> getPublicConfig(String data) {
        List<String> keys = getPublicConfigKeys(data);
        Map<String, String> ret = new HashMap<>();

        for (String key : keys) {
            ret.put(key, cntService.getValue(key));
        }

        return ret;
    }

    public static List<String> getPublicConfigKeys(String data) {
        List<String> ret = new LinkedList<String>();
        Matcher tokenMatcher = TOKEN_PATTERN.matcher(data);
        while (tokenMatcher.find()) {
            int keyStart = tokenMatcher.start(1);
            int keyEnd = tokenMatcher.end(1);
            String key = data.substring(keyStart, keyEnd);
            ret.add(key);
        }
        return ret;
    }
}
