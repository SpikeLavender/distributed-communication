package com.natsumes.demo.handler;


import com.natsumes.demo.entity.RequestBody;
import com.natsumes.demo.entity.ResponseBody;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.Callable;

/**
 * 自定义事件处理器
 */
public class RpcReqClientHandler extends ChannelInboundHandlerAdapter implements Callable {
    //1.定义成员变量
    private ChannelHandlerContext context; //事件处理器上下文（存储handler信息，写操作）

    private ResponseBody resp; // 记录服务器返回的数据

    private RequestBody req; //记录将要发送给服务器的数据

    //2.实现channelActive 客户端和服务器连接时，该方法就自动执行
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //初始化ChannelHandlerContext
        this.context = ctx;
    }


    //3.实现channelRead   当我们读到服务器数据，该方法自动执行
    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //将读到的服务器的数据msg，设置为成员变量的值
        resp = (ResponseBody) msg;
        System.out.println("Client : " + resp.getId() + ", " + resp.getName() + ", " + resp.getRespMsg());
        notify();
    }

    //4.将客户端的数据写到服务器
    @Override
    public synchronized Object call() throws Exception {
        //context给服务器写数据
        //context.writeAndFlush(req);
        context.writeAndFlush(req);
        wait();
        return resp;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause.getMessage());
        ctx.close();
    }

    //5.设置参数的方法
    public void setParam(Object param) {
        this.req = (RequestBody) param;
    }
}
