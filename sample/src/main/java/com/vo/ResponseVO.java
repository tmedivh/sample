package com.vo;

/**
 * Copyright (C), 2011-2015 温州贷
 * Date: 2018/1/24 14:45
 * Description:
 * Author: miaoyusong@wzdai.com
 */

import java.io.Serializable;

public class ResponseVO implements Serializable {
    private static final long serialVersionUID = -8145865776285690954L;
    private static final int DEFAULT_STATUS_CODE = 200;
    private static final String DEFAULT_MESSAGE = "success";
    private int code;
    private String msg;
    private Object data;

    private ResponseVO(int status_code, String message, Object data) {
        super();
        this.code = status_code;
        this.msg = message;
        this.data = data;
    }

    public static ResponseVOBuilder response() {
        return new ResponseVOBuilder();
    }

    public static ResponseVOBuilder response(int status_code, String message) {
        return new ResponseVOBuilder(status_code, message);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int status_code) {
        this.code = status_code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String message) {
        this.msg = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static class ResponseVOBuilder {
        private int code = DEFAULT_STATUS_CODE;
        private String msg = DEFAULT_MESSAGE;
        private Object data;

        public ResponseVOBuilder() {
            super();
        }

        public ResponseVOBuilder(int status_code, String message) {
            super();
            this.code = status_code;
            this.msg = message;
        }

        public ResponseVOBuilder setStatus_code(int status_code) {
            this.code = status_code;
            return this;
        }

        public ResponseVOBuilder setMessage(String message) {
            this.msg = message;
            return this;
        }

        public ResponseVOBuilder setData(Object data) {
            this.data = data;
            return this;
        }

        public ResponseVO build() {
            return new ResponseVO(code, msg, data);
        }
    }

    @Override
    public String toString() {
        return "ResponseVO{" +
                "status_code=" + code +
                ", message='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}