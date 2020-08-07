package com.natsumes.demo.service.impl;

import com.natsumes.demo.entity.RequestBody;
import com.natsumes.demo.entity.ResponseBody;
import com.natsumes.demo.handler.RpcDemoServiceHandler;
import com.natsumes.demo.handler.RpcReqServiceHandler;
import com.natsumes.demo.server.NettyServer;
import com.natsumes.demo.service.RpcDemoService;
import io.netty.bootstrap.ServerBootstrap;


import java.net.InetSocketAddress;

public class RpcDemoServiceImpl implements RpcDemoService {

    //客户端要远程调用的方法
    @Override
    public String sendRpcRequest(String request) {
        System.out.println(request);
        return "I'm fine, thank you!";
    }

    @Override
    public ResponseBody sendRpcRequest(RequestBody requestBody) {
        System.out.println(requestBody);
        ResponseBody success;
        success = new ResponseBody(200, "success");
        return success;
    }

    //创建一个方法启动服务器
    public static void startServer(String ip, int port) throws InterruptedException {
        //ServerBootstrap serverBootstrap = NettyServer.createNettyServer(new RpcDemoServiceHandler());

        ServerBootstrap serverBootstrap = NettyServer.createNettyServer(new RpcReqServiceHandler());
        //4.启动引导类绑定端口
        serverBootstrap.bind(new InetSocketAddress(ip, port)).sync();
    }
}
