package com.exception;

/**
 * Copyright (C), 2011-2015 温州贷
 * FileName: com.wzdai.sms.exception.SmsException.java
 * Author: autumn
 * Email: autumnkuang@wzdai.com
 * Date: 2015/7/6 17:22
 * Description:
 * History:
 * <Author>      <Time>    <version>    <desc>
 * kuangqiuyong   17:22    1.0          Create
 */
public class ShiroException extends RuntimeException {

    public ShiroException() {
        super();
    }

    public ShiroException(String message) {
        super(message);
    }

    public ShiroException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShiroException(Throwable cause) {
        super(cause);
    }

    protected ShiroException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
