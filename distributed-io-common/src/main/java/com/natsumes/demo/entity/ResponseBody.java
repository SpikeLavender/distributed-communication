package com.natsumes.demo.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseBody implements Serializable {

    private static final long serialVersionUID = -5176829430289351495L;

    private String id;

    private String name;

    private String respMsg;

}
