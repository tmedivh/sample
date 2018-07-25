package com.session;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;

public class HttpSessionWrapper implements HttpSession {

    //被包装的Session
    protected HttpSession delegate;

    private HttpSessionWrapper() {

    }

    public HttpSessionWrapper(HttpSession session) {
        if (session == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        this.delegate = session;
    }

    public HttpSession getDelegate() {
        return delegate;
    }

    public void setDelegate(HttpSession delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        this.delegate = delegate;
    }

    public boolean isWrapperFor(HttpSession wrapped) {
        if (delegate == wrapped) {
            return true;
        } else if (delegate instanceof HttpSessionWrapper) {
            return ((HttpSessionWrapper) delegate).isWrapperFor(wrapped);
        } else {
            return false;
        }
    }

    public boolean isWrapperFor(Class<?> wrappedType) {

        if (!HttpSession.class.isAssignableFrom(wrappedType)) {
            throw new IllegalArgumentException(
                    "Given class " + wrappedType.getName() + " not a subinterface of " + HttpSession.class.getName());
        }
        if (wrappedType.isAssignableFrom(delegate.getClass())) {
            return true;
        } else if (delegate instanceof HttpSessionWrapper) {
            return ((HttpSessionWrapper) delegate).isWrapperFor(wrappedType);
        } else {
            return false;
        }
    }

    @Override
    public long getCreationTime() {
        return getDelegate().getCreationTime();
    }

    @Override
    public String getId() {
        return getDelegate().getId();
    }

    @Override
    public long getLastAccessedTime() {
        return getDelegate().getLastAccessedTime();
    }

    @Override
    public ServletContext getServletContext() {
        return getDelegate().getServletContext();
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        getDelegate().setMaxInactiveInterval(interval);
    }

    @Override
    public int getMaxInactiveInterval() {
        return getDelegate().getMaxInactiveInterval();
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return getDelegate().getSessionContext();
    }

    @Override
    public Object getAttribute(String name) {
        return getDelegate().getAttribute(name);
    }

    @Override
    public Object getValue(String name) {
        return getDelegate().getValue(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return getDelegate().getAttributeNames();
    }

    @Override
    public String[] getValueNames() {
        return getDelegate().getValueNames();
    }

    @Override
    public void setAttribute(String name, Object value) {
        getDelegate().setAttribute(name, value);
    }

    @Override
    public void putValue(String name, Object value) {
        getDelegate().putValue(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        getDelegate().removeAttribute(name);
    }

    @Override
    public void removeValue(String name) {
        getDelegate().removeValue(name);
    }

    @Override
    public void invalidate() {
        getDelegate().invalidate();
    }

    @Override
    public boolean isNew() {
        return getDelegate().isNew();
    }
}
