package com.natsumes.demo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

public class NettyServer {

    public static void main(String[] args) throws InterruptedException {



        ChannelHandler channelHandler = new SimpleChannelInboundHandler<String>() {

            protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
                //获取入栈信息，打印客户端传递的数据
                System.out.println(s);
            }


            protected void messageReceived(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
                //获取入栈信息，打印客户端传递的数据
                System.out.println(msg);
            }
        };

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
                        pipeline.addFirst(new StringEncoder());
                        pipeline.addLast(new StringDecoder());
                        //绑定业务逻辑
                        pipeline.addLast(channelHandler);
                    }
                });

        //4.启动引导类绑定端口
        ChannelFuture future = serverBootstrap.bind(new InetSocketAddress("127.0.0.1", 30568)).sync();
        //5.关闭通道
        future.channel().closeFuture();
    }

    public static ServerBootstrap createNettyServer(ChannelHandler channelHandler,
                                                    ChannelHandler encoder,
                                                    ChannelHandler decoder) {
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
                        pipeline.addLast(encoder);
                        pipeline.addLast(decoder);
                        //绑定业务逻辑
                        pipeline.addLast(channelHandler);
                    }
                });
        return serverBootstrap;
    }
}
