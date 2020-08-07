package com.natsumes.demo.entity;

import java.io.Serializable;

public class RequestBody implements Serializable {

    private static final long serialVersionUID = 4436451078723363719L;

    private String name;

    private String message;

    public RequestBody() {
    }

    @Override
    public String toString() {
        return "RequestBody{" +
                "name='" + name + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public RequestBody(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
