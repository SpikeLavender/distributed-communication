package com.natsumes.demo.handler;

import com.natsumes.demo.entity.RequestBody;
import com.natsumes.demo.entity.ResponseBody;
import com.natsumes.demo.service.RpcDemoService;
import com.natsumes.demo.service.impl.RpcDemoServiceImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class RpcReqServiceHandler extends ChannelInboundHandlerAdapter {

    //当客户端读取数据时，该方法会被调用
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //注意: 客户端将来发送请求的时候会传递一个参数: RpcDemoService#sendRpcRequest#server, are you ok?
        //1.判断当前的请求是否符合规则 msg.toString().startsWith("RpcDemoService")
        //RequestBody req = (RequestBody) msg;
        //if (msg.toString().startsWith("RpcDemoService")) {
            //2.如果符合规则，调用实现类获取到一个result
            RpcDemoService service = new RpcDemoServiceImpl();

            String result = service.sendRpcRequest(msg.toString());

            //3.把调用实现类的方法获取到的result写到客户端
            ctx.writeAndFlush(result);
        //}
    }
}
