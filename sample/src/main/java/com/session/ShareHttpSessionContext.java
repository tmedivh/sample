package com.session;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;

/*
 * Copyright (C), 2011-2015, 温州贷
 * Author:   autumn
 * Date:     14-11-6
 * Description: 模块目的、功能描述
 * History: 变更记录
 * <author>           <time>             <version>        <desc>
 * autumn           14-11-6           00000001         创建文件
 *
 */
public class ShareHttpSessionContext implements HttpSessionContext {

    private HttpSessionContext delegate;

    private ShareHttpSessionContext(){

    }

    public ShareHttpSessionContext(HttpSessionContext delegate){
        this.delegate = delegate;
    }



    @Override
    public HttpSession getSession(String sessionId) {
//        HttpSession httpSession = delegate.getSession(sessionId);
//        if( httpSession == null )
//            return null;
//        return new ShareSession(delegate.getSession(sessionId));
        throw new RuntimeException("共享session暂不支持此方法");
    }

    @Override
    public Enumeration<String> getIds() {
        return delegate.getIds();
    }
}
