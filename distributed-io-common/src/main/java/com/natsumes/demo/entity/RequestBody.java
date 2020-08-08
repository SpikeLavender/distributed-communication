package com.natsumes.demo.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class RequestBody implements Serializable {

    private static final long serialVersionUID = 4436451078723363719L;

    private String id;

    private String name;

    private String reqMsg;

}
