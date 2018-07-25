package com.session;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import java.net.URLDecoder;
import java.util.*;

public class ShareSessionRequest extends HttpServletRequestWrapper {

    private ShareSession session;

    public ShareSessionRequest(HttpServletRequest request, CacheManager cacheManager, String domain, boolean cacheAutoDestroy) {
        super(request);
        this.session = new ShareSession(request.getSession(), cacheManager, domain, cacheAutoDestroy);
    }

    @Override
    public String getParameter(String name) {
        String param = super.getParameter(name);
        if (StringUtils.hasText(param) && param.contains("%")) {
            return URLDecoder.decode(param);
        }

        return param;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] paramVals = super.getParameterValues(name);

        if (paramVals != null && paramVals.length > 0) {
            String[] params = new String[paramVals.length];
            List<String> vals = new ArrayList<String>();

            for (String paramVal : paramVals) {
                if (StringUtils.hasText(paramVal) && paramVal.contains("%")) {
                    vals.add(URLDecoder.decode(paramVal));
                } else {
                    vals.add(paramVal);
                }
            }
            return vals.toArray(params);
        }

        return paramVals;
    }

    @Override
    public Map<String, String[]> getParameterMap() {

        Map<String, String[]> params = super.getParameterMap();

        if (params != null && !params.isEmpty()) {
            Map<String, String[]> params2 = new LinkedHashMap<String, String[]>();

            Iterator<String> iterator = params.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String[] paramVals = params.get(key);

                if (paramVals != null && paramVals.length > 0) {
                    String[] paramValsTmp = new String[paramVals.length];
                    List<String> vals = new ArrayList<String>();
                    for (String paramVal : paramVals) {
                        if (StringUtils.hasText(paramVal) && paramVal.contains("%")) {
                            vals.add(URLDecoder.decode(paramVal));
                        } else {
                            vals.add(paramVal);
                        }
                    }
                    params2.put(key, vals.toArray(paramValsTmp));

                } else {
                    params2.put(key, paramVals);
                }
            }

            return params2;
        }
        return params;
    }

    @Override
    public HttpSession getSession() {
        return this.session;
    }

    @Override
    public HttpSession getSession(boolean create) {
        HttpSession httpSession = super.getSession(create);
        this.session = new ShareSession(httpSession, this.session.getCacheImpl(), this.session.getId(), this.session.isCacheAutoDestroy());
        return this.session;
    }
}
