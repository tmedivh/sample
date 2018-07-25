package com.session;

import com.utils.RedisUtils;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheImpl implements CacheManager {

    @Override
    public String[] keys(String domainKey) {
        return boundHashOps(domainKey).keys().toArray(new String[]{});
    }

    @Override
    public void put(String domainKey, String key, Object value) {
        boundHashOps(domainKey).put(key, value);
    }

    @Override
    public void delete(String domainKey, String key) {
        boundHashOps(domainKey).delete(key);
    }

    @Override
    public void invalidate(String domainKey) {
        RedisUtils.delete(domainKey);
    }

    @Override
    public Object get(String domainKey, String key) {
        return boundHashOps(domainKey).get(key);
    }

    @Override
    public Map<String, Object> entries(String domainKey) {
        return boundHashOps(domainKey).entries();
    }

    @Override
    public void expire(String domainKey, long timeout, TimeUnit timeUnit) {
        boundHashOps(domainKey).expire(timeout, timeUnit);
    }

    @Override
    public boolean hasKey(String domainKey) {
        return RedisUtils.hasKey(domainKey);
    }

    @Override
    public void persist(String domainKey) {
        boundHashOps(domainKey).persist();
    }

    private static BoundHashOperations<String, String, Object> boundHashOps(String domainKey) {
        BoundHashOperations<String, String, Object> boundHashOps = (BoundHashOperations) RedisUtils.boundHashOps(domainKey);
        return boundHashOps;
    }
}
