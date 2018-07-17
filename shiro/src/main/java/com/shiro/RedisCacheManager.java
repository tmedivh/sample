package com.shiro;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Copyright (C), 2011-2018 温州贷
 * Author: miaoyusong@wzdai.com
 * Date: 2018-07-17-16:47
 * Description:
 */
public class RedisCacheManager implements CacheManager {

    @Autowired
    private RedisTemplate redisTemplate; // RedisTemplate，如果不明白怎么使用的，请参考http://blog.csdn.net/liuchuanhong1/article/details/54601037

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        System.out.println("name:" + name);
        return new RedisCache<K, V>(600, redisTemplate);// 为了简化代码的编写，此处直接new一个Cache
    }
}
