package com.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.query.SortQuery;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {

    private static RedisTemplate redisTemplate;

    /**
     * 设置 redisTemplate
     *
     * @param redisTemplate
     */
    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        RedisUtils.redisTemplate = redisTemplate;
    }

    /**
     * 获取 redisTemplate
     *
     * @return
     */
    public static RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    /**
     * 事务外执行redis操作( 在连接池环境中使用同一connect )
     *
     * @param sessionCallback
     * @param <T>
     * @return
     */
    public static <T> T execute(SessionCallback<T> sessionCallback) {

        return (T) getRedisTemplate().execute(sessionCallback);
    }

    /**
     * 事务内执行redis操作
     *
     * @param sessionCallback
     * @param <T>
     * @return
     */
    public static <T> T executeInTransactional(final SessionCallback<T> sessionCallback) {

        SessionCallback transactionalSessionCallback = new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                operations.multi();
                Object result = sessionCallback.execute(operations);
                operations.exec();
                return result;
            }
        };
        return (T) execute(transactionalSessionCallback);
    }

    /**
     * 获取指定 key 的剩余到期时间
     *
     * @param key
     * @return
     */
    public static Long getExpire(Object key) {
        return getRedisTemplate().getExpire(key);
    }

    /**
     * 获取指定 key 的剩余到期时间
     *
     * @param key
     * @param timeUnit 时间的单位
     * @return
     */
    public static Long getExpire(Object key, TimeUnit timeUnit) {
        return getRedisTemplate().getExpire(key, timeUnit);
    }

    /**
     * 设置指定key 指定时间后期到期
     *
     * @param key
     * @param timeout 多久时间后到期
     * @param unit    时间单位
     * @return
     */
    public static boolean expire(Object key, long timeout, final TimeUnit unit) {
        return getRedisTemplate().expire(key, timeout, unit);
    }

    /**
     * 设置指定 key 在指定时间到期
     *
     * @param key
     * @param date 指定的时间
     * @return
     */
    public static boolean expireAt(Object key, java.util.Date date) {
        return getRedisTemplate().expireAt(key, date);
    }

    /**
     * 是否存在某个 key
     *
     * @param key
     * @return
     */
    public static boolean hasKey(Object key) {

        return getRedisTemplate().hasKey(key);
    }

    public static boolean persist(Object key) {

        return getRedisTemplate().persist(key);
    }


    /**
     * 重命名指定的 key
     *
     * @param oldKey 原始key
     * @param newKey 新key
     * @return
     */
    public static boolean renameIfAbsent(Object oldKey, Object newKey) {
        if (oldKey == null || newKey == null)
            return false;
        if (!oldKey.getClass().equals(newKey.getClass()))
            return false;

        return getRedisTemplate().renameIfAbsent(oldKey, newKey);
    }

    /**
     * 重命名指定的 key
     *
     * @param oldKey 原始key
     * @param newKey 新key
     * @return
     */
    public static void rename(Object oldKey, Object newKey) {
        if (oldKey == null || newKey == null)
            return;
        if (!oldKey.getClass().equals(newKey.getClass()))
            return;

        getRedisTemplate().rename(oldKey, newKey);
    }

    /**
     * pubsub functionality on the template
     *
     * @param channel
     * @param message
     */
    public static void convertAndSend(String channel, Object message) {

        getRedisTemplate().convertAndSend(channel, message);
    }


    /**
     * 获取 指定表达式的 keys
     *
     * @param pattern 表达式
     * @return
     */
    public static Set keys(Object pattern) {

        return getRedisTemplate().keys(pattern);
    }

    /**
     * 删除指定 key
     *
     * @param key
     */
    public static void delete(Object key) {

        getRedisTemplate().delete(key);
    }

    /**
     * 删除指定集合的 key
     *
     * @param key
     */
    public static void delete(Collection key) {

        getRedisTemplate().delete(key);
    }

    /**
     * 生成随机 key
     *
     * @return
     */
    public static Object randomKey() {

        return getRedisTemplate().randomKey();
    }

    /**
     * 把指定可以 移动到指定的 db
     *
     * @param key     指定的key
     * @param dbIndex db的索引
     * @return
     */
    public static Boolean move(Object key, int dbIndex) {
        return getRedisTemplate().move(key, dbIndex);
    }


    /**
     * 绑定一个 key 并对他进行hash类的操作(  操作 map )
     *
     * @param key
     * @param <HK>
     * @param <HV>
     * @return
     */
    public static <H, HK, HV> BoundHashOperations<H, HK, HV> boundHashOps(Object key) {
        return getRedisTemplate().boundHashOps(key);
    }

    /**
     * 绑定一个 key 并对他进行set类的操作(  操作 set )
     *
     * @param key
     * @return
     */
    public static BoundZSetOperations boundZSetOps(Object key) {
        return getRedisTemplate().boundZSetOps(key);
    }

    /**
     * 排序后的集合
     *
     * @param query
     * @param bulkMapper
     * @param <T>
     * @return
     */
    public static <T> List<T> sort(SortQuery query, BulkMapper bulkMapper) {
        return getRedisTemplate().sort(query, bulkMapper);
    }

    /**
     * 执行redis 操作
     *
     * @param action           操作
     * @param exposeConnection 是否使用原生的redis方法
     * @return
     */
    public static Object execute(RedisCallback action, boolean exposeConnection) {
        return getRedisTemplate().execute(action, exposeConnection);
    }


    /**
     * 获取操作句柄
     *
     * @return
     */
    public static ValueOperations opsForValue() {
        return getRedisTemplate().opsForValue();
    }

    /**
     * 绑定指定 key 并获取其set操作的句柄
     *
     * @param key
     * @return
     */
    public static BoundSetOperations boundSetOps(Object key) {
        return getRedisTemplate().boundSetOps(key);
    }

    /**
     * 获取指定 key 的数据存储类型
     *
     * @param key
     * @return
     */
    public static DataType type(Object key) {
        return getRedisTemplate().type(key);
    }

    /**
     * 排序
     *
     * @param query
     * @param storeKey
     * @return
     */
    public static Long sort(SortQuery query, Object storeKey) {
        return getRedisTemplate().sort(query, storeKey);
    }

    /**
     * 执行 redis 操作
     *
     * @param action
     * @return
     */
    public static List<Object> executePipelined(RedisCallback action) {
        return getRedisTemplate().executePipelined(action);
    }

    /**
     * 获取 hash 操作的句柄
     *
     * @param <HK>
     * @param <HV>
     * @return
     */
    public static <HK, HV> HashOperations opsForHash() {
        return getRedisTemplate().opsForHash();
    }

    /**
     * 排序后的集合
     *
     * @param query
     * @return
     */
    public static List sort(SortQuery query) {
        return getRedisTemplate().sort(query);
    }

    /**
     * zset 操作的句柄
     *
     * @return
     */
    public static ZSetOperations opsForZSet() {
        return getRedisTemplate().opsForZSet();
    }

    /**
     * 获取 set 操作的句柄
     *
     * @return
     */
    public static SetOperations opsForSet() {
        return getRedisTemplate().opsForSet();
    }

    /**
     * 获取 list 操作
     *
     * @return
     */
    public static ListOperations opsForList() {
        return getRedisTemplate().opsForList();
    }

    /**
     * 获取指定 key 的操作句柄
     *
     * @param key
     * @return
     */
    public static BoundValueOperations boundValueOps(Object key) {
        return getRedisTemplate().boundValueOps(key);
    }

    /**
     * 获取指定 key 的list操作句柄
     *
     * @param key
     * @return
     */
    public static BoundListOperations boundListOps(Object key) {
        return getRedisTemplate().boundListOps(key);
    }

    /**
     * 执行 redis 操作
     *
     * @param session
     * @return
     */
    public static List<Object> executePipelined(SessionCallback session) {
        return getRedisTemplate().executePipelined(session);
    }


    /*###########################################################  不经常使用功能暂不开放  ########################################################################*/

//    /**
//     * 排序
//     * @param query
//     * @param resultSerializer
//     * @param <T>
//     * @return
//     */
//    public static <T> List<T> sort(SortQuery query, RedisSerializer resultSerializer) {
//        return getRedisTemplate().sort(query, resultSerializer);
//    }
//    public static Object execute(RedisCallback action, boolean exposeConnection, boolean pipeline) {
//        return getRedisTemplate().execute(action, exposeConnection, pipeline);
//    }
//
//    public static Object execute(RedisScript script, List keys, Object... args) {
//        return getRedisTemplate().execute(script, keys, args);
//    }
//
//    public static List<Object> executePipelined(RedisCallback action, RedisSerializer resultSerializer) {
//        return getRedisTemplate().executePipelined(action, resultSerializer);
//    }
//    public static <T, S> List<T> sort(SortQuery query, BulkMapper bulkMapper, RedisSerializer resultSerializer) {
//        return getRedisTemplate().sort(query, bulkMapper, resultSerializer);
//    }
//
//    public static Object execute(RedisScript script, RedisSerializer argsSerializer, RedisSerializer resultSerializer, List keys, Object... args) {
//        return getRedisTemplate().execute(script, argsSerializer, resultSerializer, keys, args);
//    }
//
//    public static List<Object> executePipelined(SessionCallback session, RedisSerializer resultSerializer) {
//        return getRedisTemplate().executePipelined(session, resultSerializer);
//    }
//
//    public static byte[] dump(Object key){
//
//        return getRedisTemplate().dump(key);
//    }
//
//    public static void restore(Object key,  byte[] value, long timeToLive, TimeUnit unit){
//
//        getRedisTemplate().restore(key, value, timeToLive, unit);
//    }
    /*###########################################################  不经常使用功能暂不开放  ########################################################################*/





    /*###########################################################  配置类功能暂不开放  ########################################################################*/

//
//    public static void killClient( String host,  int port){
//        getRedisTemplate().killClient(host,port);
//    }
//
//    public static void afterPropertiesSet() {
//        getRedisTemplate().afterPropertiesSet();
//    }
//
//    public static void setEnableTransactionSupport(boolean enableTransactionSupport) {
//        getRedisTemplate().setEnableTransactionSupport(enableTransactionSupport);
//    }
//
//    public static void unwatch() {
//        getRedisTemplate().unwatch();
//    }
//
//    public static void discard() {
//        getRedisTemplate().discard();
//    }
//
//    public static RedisSerializer<?> getHashKeySerializer() {
//        return getRedisTemplate().getHashKeySerializer();
//    }
//
//    public static void setExposeConnection(boolean exposeConnection) {
//        getRedisTemplate().setExposeConnection(exposeConnection);
//    }
//
//    public static void setHashValueSerializer(RedisSerializer hashValueSerializer) {
//        getRedisTemplate().setHashValueSerializer(hashValueSerializer);
//    }
//
//    public static void setDefaultSerializer(RedisSerializer serializer) {
//        getRedisTemplate().setDefaultSerializer(serializer);
//    }
//
//    public static void watch(Object key) {
//        getRedisTemplate().watch(key);
//    }
//
//    public static RedisSerializer<?> getHashValueSerializer() {
//        return getRedisTemplate().getHashValueSerializer();
//    }
//
//    public static List<RedisClientInfo> getClientList() {
//        return getRedisTemplate().getClientList();
//    }
//
//    public static List<Object> exec() {
//        return getRedisTemplate().exec();
//    }
//
//    public static void setScriptExecutor(ScriptExecutor scriptExecutor) {
//        getRedisTemplate().setScriptExecutor(scriptExecutor);
//    }
//
//    public static void setValueSerializer(RedisSerializer serializer) {
//        getRedisTemplate().setValueSerializer(serializer);
//    }
//
//    public static RedisSerializer<?> getValueSerializer() {
//        return getRedisTemplate().getValueSerializer();
//    }
//
//    public static void setConnectionFactory(RedisConnectionFactory connectionFactory) {
//        getRedisTemplate().setConnectionFactory(connectionFactory);
//    }
//
//    public static void setKeySerializer(RedisSerializer serializer) {
//        getRedisTemplate().setKeySerializer(serializer);
//    }
//
//    public static RedisConnectionFactory getConnectionFactory() {
//        return getRedisTemplate().getConnectionFactory();
//    }
//
//    public static void setEnableDefaultSerializer(boolean enableDefaultSerializer) {
//        getRedisTemplate().setEnableDefaultSerializer(enableDefaultSerializer);
//    }
//
//    public static void setStringSerializer(RedisSerializer stringSerializer) {
//        getRedisTemplate().setStringSerializer(stringSerializer);
//    }
//
//    public static void slaveOfNoOne(){
//        getRedisTemplate().slaveOfNoOne();
//    }
//
//    public static void multi() {
//        getRedisTemplate().multi();
//    }
//
//    public static void slaveOf(String host, int port) {
//        getRedisTemplate().slaveOf(host, port);
//    }
//
//    public static void watch(Collection keys) {
//        getRedisTemplate().watch(keys);
//    }
//
//    public static RedisSerializer<?> getKeySerializer() {
//        return getRedisTemplate().getKeySerializer();
//    }
//
//    public static boolean isExposeConnection() {
//        return getRedisTemplate().isExposeConnection();
//    }
//
//    public static List<Object> exec(RedisSerializer valueSerializer) {
//        return getRedisTemplate().exec(valueSerializer);
//    }
//
//    public static RedisSerializer<?> getDefaultSerializer() {
//        return getRedisTemplate().getDefaultSerializer();
//    }
//
//    public static void setHashKeySerializer(RedisSerializer hashKeySerializer) {
//        getRedisTemplate().setHashKeySerializer(hashKeySerializer);
//    }
//
//    public static boolean isEnableDefaultSerializer() {
//        return getRedisTemplate().isEnableDefaultSerializer();
//    }
//
//    public static RedisSerializer<String> getStringSerializer() {
//        return getRedisTemplate().getStringSerializer();
//    }

    /*###########################################################  配置类功能暂不开放  ########################################################################*/
}