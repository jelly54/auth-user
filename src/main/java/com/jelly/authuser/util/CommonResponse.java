package com.jelly.authuser.util;


import com.jelly.authuser.exception.ErrorEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * @author guodongzhang
 */
public class CommonResponse {
    public static final String SUCCESS_STATUS = "success";
    public static final String FAILED_STATUS = "failed";

    private String status;
    private Object data;
    private Message message;

    /**
     * Spring Cloud Feign 需要
     */
    public CommonResponse() {
    }

    private CommonResponse(Builder builder) {
        this.status = builder.status;
        this.data = builder.data;
        this.message = builder.message;
    }

    public static class Builder {
        private String status = SUCCESS_STATUS;
        private Object data;
        private Message message;

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public Builder data(String desc, Object data) {
            Map<String, Object> map = new HashMap<>(2);
            map.put(desc, data);
            this.data = map;
            return this;
        }

        public Builder message(int errno, String description) {
            this.message = new Message(errno, description);
            return this;
        }

        public CommonResponse build() {
            return new CommonResponse(this);
        }
    }

    private static class Message {
        private int errno;
        private String description;

        public Message() {
        }

        public Message(int errno, String description) {
            this.errno = errno;
            this.description = description;
        }

        public int getErrno() {
            return errno;
        }

        public void setErrno(int errno) {
            this.errno = errno;
        }

        public String getDescription() {
            return description;
        }

        public void setDescribtion(String description) {
            this.description = description;
        }
    }

    private static Builder createBuilder() {
        return new Builder();
    }

    public static Builder ok() {
        return createBuilder();
    }

    public static Builder ok(Object data) {
        return ok().data(data);
    }

    public static Builder error() {
        return createBuilder().status(FAILED_STATUS);
    }

    public static Builder error(int errno, String message) {
        return error().message(errno, message);
    }

    public static Builder error(ErrorEnum errorEnum) {
        return error(errorEnum, "");
    }

    public static Builder error(ErrorEnum errorEnum, Object... msgArgs) {
        StringBuilder message = new StringBuilder();
        for (Object msg : msgArgs) {
            message.append(";").append(msg.toString());
        }
        return error().message(errorEnum.code(), message.insert(0, errorEnum.message()).toString());
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
