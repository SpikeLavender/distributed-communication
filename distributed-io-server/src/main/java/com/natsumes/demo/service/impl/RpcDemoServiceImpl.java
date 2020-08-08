package com.natsumes.demo.service.impl;

import com.natsumes.demo.entity.RequestBody;
import com.natsumes.demo.entity.ResponseBody;
import com.natsumes.demo.handler.RpcReqServiceHandler;
import com.natsumes.demo.service.RpcDemoService;
import com.natsumes.demo.utils.SerializeUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;


import java.net.InetSocketAddress;

public class RpcDemoServiceImpl implements RpcDemoService {

    //客户端要远程调用的方法
    @Override
    public String sendRpcRequest(String request) {
        System.out.println(request);
        return "I'm fine, thank you!";
    }

    @Override
    public ResponseBody sendRpcRequest(RequestBody req) {
        System.out.println(req);
        ResponseBody resp = new ResponseBody();
        resp.setId(req.getId());
        resp.setName("resp" + req.getId());
        resp.setRespMsg("响应内容" + req.getId());
        return resp;
    }

    //创建一个方法启动服务器
    public static void startServer(String ip, int port) throws InterruptedException {
        //1.创建2个线程池对象
        //bossGroup 负责接收用户连接
        NioEventLoopGroup baseGroup = new NioEventLoopGroup();
        //workGroup 负责处理用户的io读写操作
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        //2.创建启动引导类
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        //3.设置启动引导类
        //添加到组中，两个线程池，第一个位置的线程池负责接收，第二个就负责读写
        serverBootstrap.group(baseGroup, workGroup)
                //给我们当前设置一个通道类型
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                //绑定一个初始化监听
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    //事件监听channel通道
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        //获取pipeLine
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        //绑定编码
                        pipeline.addLast(new ObjectEncoder());
                        pipeline.addLast(new ObjectDecoder(1024* 1024,
                                ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                        //绑定业务逻辑
                        pipeline.addLast(new RpcReqServiceHandler());
                    }
                });

        //4.启动引导类绑定端口
        serverBootstrap.bind(new InetSocketAddress(ip, port)).sync();
    }
}
