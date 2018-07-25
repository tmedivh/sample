package com.session;


import org.slf4j.Logger;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ShareSession extends HttpSessionWrapper {
    //日志
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(ShareSession.class);
    //共享组件接口
    private CacheManager cacheManager;
    //共享域，可视为共享session的id
    private String domain;
    //代理session的id
    private String delegateId;
    //是否自动销毁
    private boolean cacheAutoDestroy;
    //session 创建时间
    private long creationTime;
    //最后访问时间
    private long lastAccessedTime;
    //session最大存活时间
    private int maxInactiveInterval;
    //是否共享session
    private boolean share = true;

    //session存储在缓存服务器的 命名空间
    public static final String domainKey = "entries";
    //缓存实现的key
    public static final String cacheKey = "cacheImpl";
    //共享session的最后读写操作时间的Key
    private static final String lastWriteActionTimeKey = "lastWriteActionTime";
    //共享session的创建时间KEY
    private static final String creationTimeKey = "creationTime";
    //共享session的最大存活时间的Key
    private static final String maxInactiveIntervalKey = "maxInactiveInterval";

    @Override
    public long getCreationTime() {
        return cacheManager.hasKey(this.domain) ? (Long) this.cacheManager.get(domain, creationTimeKey) : creationTime;
    }

    @Override
    public long getLastAccessedTime() {
        return cacheManager.hasKey(this.domain) ? (Long) this.cacheManager.get(domain, lastWriteActionTimeKey) : lastAccessedTime;
    }

    @Override
    public int getMaxInactiveInterval() {
        return cacheManager.hasKey(this.domain) ? (Integer) this.cacheManager.get(domain, maxInactiveIntervalKey) : maxInactiveInterval;
    }

    public CacheManager getCacheImpl() {
        return cacheManager;
    }

    public ShareSession(HttpSession delegate) {
        this(delegate, (String) delegate.getAttribute(domainKey));
    }

    public ShareSession(HttpSession delegate, String domain) {
        this(delegate, (CacheManager) delegate.getAttribute(cacheKey), domain, true);
    }

    public ShareSession(HttpSession delegate, CacheManager cacheManager, String domain, boolean cacheAutoDestroy) {

        super(delegate);
        this.cacheManager = cacheManager;
        this.cacheAutoDestroy = cacheAutoDestroy;
        if (cacheAutoDestroy) {
            this.maxInactiveInterval = delegate.getMaxInactiveInterval();
        } else {
            this.maxInactiveInterval = -1;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("//=====请求的sessionId:" + domain + "==========//");
        }

        this.delegateId = this.delegate.getId();
        this.domain = StringUtils.isEmpty(domain) ? this.delegateId : domain;

        if (isShare() && this.cacheAutoDestroy && !this.domain.equals(this.delegateId)) {
            this.changeAttribute(this.domain, this.delegateId);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("//=======请求的当前会话sessionId为:" + this.domain + "==========//");
        }

        if (null == this.cacheManager) {
            if (logger.isWarnEnabled()) {
                logger.warn(" session 共享功能没有找到缓存插件 ");
            }
        }

        if (isShare()) {

            //对当前session取自动销毁值
            Boolean autoDestroy = (Boolean) this.getAttribute("cacheAutoDestroy");
            if (autoDestroy != null) {
                if (autoDestroy || cacheManager.get(this.domain, "OLD_USER_INFO") == null) {
                    this.maxInactiveInterval = delegate.getMaxInactiveInterval();
                } else {
                    this.maxInactiveInterval = -1;
                }

            } else {
                this.setAttribute("cacheAutoDestroy", cacheAutoDestroy);
            }

            this.delegate.setAttribute(domainKey, this.domain);
            this.delegate.setAttribute(cacheKey, this.cacheManager);

            //最后访问时间
            //this.updateLastWriteActionTime();
            //重置session失效时间
            //this.resetExpireOfSession();

        }

    }


    @Override
    public HttpSessionContext getSessionContext() {
        return new ShareHttpSessionContext(delegate.getSessionContext());
    }


    @Override
    public String getId() {

        if (isShare()) {
            return this.domain;
        } else {
            return this.delegate.getId();
        }
    }

    /*
       切换session
       当用户访问到集群中的另一台主机时，由于产生了新的session，此时需要将老session中的值转移到新session中
     */
    private void changeAttribute(String oldSessionDomain, String newSessionDomain) {
        this.domain = newSessionDomain;

        try {
            if (!cacheManager.hasKey(oldSessionDomain)) {
                return;
            }

            Map<String, Object> entries = cacheManager.entries(oldSessionDomain);
            if (entries == null || entries.isEmpty()) return;

            Set<String> set = entries.keySet();
            Iterator<String> iterable = set.iterator();
            while (iterable.hasNext()) {
                String key = iterable.next();
                cacheManager.put(newSessionDomain, key, entries.get(key));
            }

            //老session 30秒失效
            cacheManager.expire(oldSessionDomain, 30, TimeUnit.SECONDS);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    @Override
    public void setAttribute(String key, Object value) {

        if (isShare() && !(value instanceof NoShare)) {

            try {
                if (!cacheManager.hasKey(domain)) {
                    this.creationTime = this.delegate.getCreationTime();
                    cacheManager.put(domain, creationTimeKey, this.creationTime);
                    cacheManager.put(domain, maxInactiveIntervalKey, this.maxInactiveInterval);
                    this.updateLastWriteActionTime();
                    this.resetExpireOfSession();
                }

                //写数据到缓存服务器
                cacheManager.put(domain, key, value);

            } catch (Exception e) {
                if (logger.isWarnEnabled()) {
                    logger.warn(e.getMessage(), e);
                }
            }

        } else {
            delegate.setAttribute(key, value);
        }
    }

    @Override
    public Object getAttribute(String key) {

        if (isShare()) {
            try {
                //从缓存服务中获取属性值
                Object val = cacheManager.get(domain, key);
                return val;
            } catch (Exception e) {

                if (logger.isWarnEnabled()) {
                    logger.warn(e.getMessage(), e);
                }
                return null;
            }
        } else {
            return delegate.getAttribute(key);
        }
    }

    @Override
    public void removeAttribute(String key) {

        if (isShare()) {
            try {
                //从缓存服务器删除数据
                cacheManager.delete(domain, key);
            } catch (Exception e) {

                if (logger.isWarnEnabled()) {
                    logger.warn(e.getMessage(), e);
                }
            }

        } else {
            delegate.removeAttribute(key);
        }

    }


    @Override
    public void putValue(String s, Object o) {
        setAttribute(s, o);
    }

    @Override
    public void removeValue(String key) {
        removeAttribute(key);
    }

    @Override
    public Object getValue(String key) {
        return getAttribute(key);
    }

    /**
     * 销毁 SESSION 方法
     * 销毁方法实际调用的 为  delegate.invalidate()
     * 只有手动调用此销毁方法的销毁操作才会同步到缓存服务器
     * session过期的销毁方法不会同步到缓存服务器( 因为缓存服务器的ttl时间与session的过期时间相同 ),
     * 以此解决关闭浏览器重新打开所产生的无效session过期导致的共享session(缓存服务器session失效问题)
     */
    @Override
    public void invalidate() {

        //销毁session
        if (isShare()) {
            try {
                cacheManager.invalidate(domain);
            } catch (Exception e) {
                if (logger.isWarnEnabled()) {
                    logger.warn(e.getMessage(), e);
                }
            }
        } else {
            delegate.invalidate();
        }

    }

    /**
     * 获取 session 还有多少秒过期
     * 如果小于 1 则永不过期
     **/
    private int getExpire() {

        if (isShare()) {
            if (cacheAutoDestroy) {
                return this.getMaxInactiveInterval() - new Long((System.currentTimeMillis() - this.getLastAccessedTime()) / 1000).intValue();
            } else {
                return -1;
            }
        } else {
            return delegate.getMaxInactiveInterval() - new Long((System.currentTimeMillis() - delegate.getLastAccessedTime()) / 1000).intValue();
        }

    }

    public boolean isShare() {
        return share;
    }

    public void setShare(boolean share) {
        this.share = share;
    }


    /*
     *更新缓存服务器的最后读写操作时间
     */
    private void updateLastWriteActionTime() {

        try {
            if (this.cacheManager.hasKey(this.domain)) {
                this.lastAccessedTime = System.currentTimeMillis();
                cacheManager.put(this.domain, lastWriteActionTimeKey, this.lastAccessedTime);
            }
        } catch (Exception e) {
            if (logger.isWarnEnabled()) {
                logger.warn(e.getMessage(), e);
            }
        }
    }

    /*
     * 重置session失效时长
     */
    private void resetExpireOfSession() {

        if (this.cacheManager.hasKey(this.domain)) {
            //更新缓存失效的时间 实现续期功能
            updateCacheExpire((long) this.maxInactiveInterval, TimeUnit.SECONDS);
        }
    }

    /*
     * 更新缓存的过期时间
     */
    private void updateCacheExpire(Long interval, TimeUnit timeUnit) {

        if (interval > 0) {
            try {
                cacheManager.expire(this.domain, interval, timeUnit);
            } catch (Exception e) {
                if (logger.isWarnEnabled()) {
                    logger.warn(e.getMessage(), e);
                }
            }

        } else {
            cacheManager.persist(this.domain);
        }

    }

    /**
     * 销毁指定共享session
     *
     * @param domain
     * @return
     */
    public static boolean destroyShareSessionByDomain(HttpSession session, String domain) {

        if (session instanceof ShareSession && StringUtils.hasText(domain)) {
            ((ShareSession) session).getCacheImpl().invalidate(domain);
            return true;
        } else if (logger.isWarnEnabled()) {
            logger.warn("没有启动共享session");
        }
        return false;
    }


    /**
     * 立即销毁共享session
     *
     * @return
     */
    public static boolean destroyShareSession(HttpSession session) {

        if (session instanceof ShareSession) {
            ShareSession shareSession = ((ShareSession) session);
            shareSession.invalidate();

            return true;
        } else if (logger.isWarnEnabled()) {
            logger.warn("没有启动共享session");
        }

        return false;
    }


    /**
     * 与原生session同步销毁共享session
     *
     * @param session
     * @return
     */
    public static boolean destroyShareSessionByTimer(HttpSession session) {

        if (session instanceof ShareSession) {
            ShareSession shareSession = ((ShareSession) session);
            String domain = shareSession.getId();
            shareSession.getCacheImpl().expire(domain, shareSession.getMaxInactiveInterval(), TimeUnit.SECONDS);
            return true;
        } else if (logger.isWarnEnabled()) {
            logger.warn("没有启动共享session");
        }
        return false;
    }

    /**
     * 清除session的失效时间
     *
     * @param session
     * @return
     */
    public static boolean cleanShareSessionExpire(HttpSession session) {

        if (session instanceof ShareSession) {
            ShareSession shareSession = ((ShareSession) session);
            String domain = shareSession.getId();

            shareSession.getCacheImpl().persist(domain);
            return true;

        } else if (logger.isWarnEnabled()) {
            logger.warn("没有启动共享session");
        }

        return false;
    }

    /**
     * 是否自动销毁
     *
     * @return
     */
    public boolean isCacheAutoDestroy() {
        return cacheAutoDestroy;
    }
}
