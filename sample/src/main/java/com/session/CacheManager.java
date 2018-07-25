package com.session;

import java.util.concurrent.TimeUnit;

public interface CacheManager {

    String[] keys(String domainKey);

    void put(String domainKey, String key, Object o);

    void delete(String domainKey, String key);

    void invalidate(String domainKey);

    Object get(String domainKey, String key);

    //<T> T get(String domainKey, String key, T defaultValue);

    java.util.Map<String, Object> entries(String domainKey);

    void expire(String domainKey, long timeOut, TimeUnit timeUnit);

    boolean hasKey(String domainKey);

    public void persist(String domainKey);
}
