package com.natsumes.demo.server;

import com.natsumes.demo.service.impl.RpcDemoServiceImpl;

public class RpcServer {
    public static void main(String[] args) throws InterruptedException {
        RpcDemoServiceImpl.startServer("127.0.0.1", 30288);
    }
}
