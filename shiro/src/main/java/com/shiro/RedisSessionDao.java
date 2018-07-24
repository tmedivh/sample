package com.shiro;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (C), 2011-2018 温州贷
 * Author: miaoyusong@wzdai.com
 * Date: 2018-07-23-15:44
 * Description:
 */
public class RedisSessionDao extends CachingSessionDAO {

    @Autowired
    private RedisTemplate redisTemplate;// 通过构造方法注入该对象


    @Override
    public void doUpdate(Session session) throws UnknownSessionException {
        try {
            redisTemplate.opsForValue().set(session.getId().toString(), session, 30, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doDelete(Session session) {
        try {
            String key = session.getId().toString();
            redisTemplate.delete(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collection<Session> getActiveSessions() {
        return Collections.emptySet();
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        try {
            redisTemplate.opsForValue().set(session.getId().toString(), session, 30, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        Session readSession = null;
        try {
            readSession = (Session) redisTemplate.opsForValue().get(sessionId.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return readSession;
    }
}
