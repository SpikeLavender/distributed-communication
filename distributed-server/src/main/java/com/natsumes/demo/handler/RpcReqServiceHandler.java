package com.natsumes.demo.handler;

import com.natsumes.demo.entity.RequestBody;
import com.natsumes.demo.entity.ResponseBody;
import com.natsumes.demo.service.impl.RpcDemoServiceImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


public class RpcReqServiceHandler extends ChannelInboundHandlerAdapter {

    //当客户端读取数据时，该方法会被调用
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //注意: 客户端将来发送请求的时候会传递一个参数:
        RequestBody req = (RequestBody) msg;

        System.out.println("Server : " + req.getId() + ", " + req.getName() + ", " + req.getReqMsg());
        //调用实现类获取到一个result
        RpcDemoServiceImpl rpcDemoService = new RpcDemoServiceImpl();
        ResponseBody resp = rpcDemoService.sendRpcRequest(req);

        ctx.writeAndFlush(resp);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause.getMessage());
        ctx.close();
    }
}
