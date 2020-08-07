package com.natsumes.demo.entity;

import java.io.Serializable;

public class ResponseBody implements Serializable {

    private static final long serialVersionUID = -5176829430289351495L;

    private Integer statusCode;

    private String message;

    public ResponseBody() {
    }

    public ResponseBody(Integer statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ResponseBody{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
