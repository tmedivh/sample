package com.filter;

import com.session.CacheManager;
import com.session.RedisCacheImpl;
import com.session.ShareSessionRequest;
import org.apache.commons.beanutils.ConvertUtils;
import org.slf4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@WebFilter(filterName = "ShareSessionFilter", urlPatterns = "/*")
@Order(6)
public class ShareSessionFilter implements Filter {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(ShareSessionFilter.class);

    private static final String configCacheKey = "cacheImplClass";
    private static final String configCacheAutoDestroyKey = "cacheAutoDestroy";
    private static final String SESSION_TOKEN = "sessionToken";

    /***
     *是否自动销毁缓存session
     *设置 true ,即与容器的session同时销毁 (默认true)
     *设置 false,存在缓存服务器的session 永不过期,且不会与web容器session同时销毁, 需要手动销毁
     *方式 1 ShareSession.destroyShareSession 来执行同步销毁,
     *方式 2 ShareSession.destroyShareSessionByDomain( session 的域 )
     **/
    private boolean cacheAutoDestroy = false;
    private CacheManager cacheManager;

    @Override
    public void init(FilterConfig filterConfig) {

        //获取共享session 是否自动销毁
        this.cacheAutoDestroy = (Boolean) findConfigValue(filterConfig, this.configCacheAutoDestroyKey, cacheAutoDestroy);

        //获取缓存实现类
        Class cacheClass = (Class) findConfigValue(filterConfig, configCacheKey, RedisCacheImpl.class);

        if (CacheManager.class.isAssignableFrom(cacheClass)) {
            try {
                cacheManager = (CacheManager) cacheClass.newInstance();
            } catch (Exception e) {
                logger.error("错误的共享session缓存实现类{}", cacheClass, e);
                throw new IllegalArgumentException("错误的共享session缓存实现类", e);
            }
        } else {
            logger.error("错误的共享session缓存实现类{}", cacheClass);
            throw new IllegalArgumentException("错误的共享session缓存实现类");
        }
    }

    private Object findConfigValue(FilterConfig config, String paramKey, Object defaultValue) {

        String dpk = config.getInitParameter(paramKey);

        if (StringUtils.hasText(dpk)) {
            try {
                return defaultValue instanceof Class ? Class.forName(dpk)
                        : ConvertUtils.convert(dpk, defaultValue.getClass());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return defaultValue;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        try {
            boolean cacheAutoDestroy = this.cacheAutoDestroy;

            if (logger.isDebugEnabled()) {
                logger.debug("=========请求IP:========");
            }
            ShareSessionRequest shareSessionRequest = new ShareSessionRequest(req, this.cacheManager, this.getDomain(req), cacheAutoDestroy);
            chain.doFilter(shareSessionRequest, response);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            chain.doFilter(request, response);
        }
    }

    private String getDomain(HttpServletRequest request) {
        String token = request.getParameter(SESSION_TOKEN);
        return token == null ? request.getRequestedSessionId() : token;
    }

    @Override
    public void destroy() {
    }
}
