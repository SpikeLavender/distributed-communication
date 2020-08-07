package com.natsumes.demo;

import com.natsumes.demo.entity.RequestBody;
import com.natsumes.demo.handler.RpcDemoClientHandler;
import com.natsumes.demo.handler.RpcReqClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;


import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcClient {
    //1.创建一个线程池对象 ---处理我们自定义事件
    private static ExecutorService executorService =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    //2.声明一个自定义事件处理器  RpcDemoClientHandler
    private static ChannelInboundHandlerAdapter handlerAdapter;

    private static RpcDemoClientHandler rpcDemoClientHandler;

    public static RpcReqClientHandler rpcReqClientHandler;

    private static Boolean status = true;

    //3.编写方法，初始化客户端（创建连接池 bootStrap 设置bootStrap 连接服务器）
    public static void initClient(Class<?> clazz) throws Exception {

        //初始化 RpcDemoClientHandler

        handlerAdapter = (ChannelInboundHandlerAdapter) clazz.getDeclaredConstructor().newInstance();

        //创建连接池对象
        NioEventLoopGroup group = new NioEventLoopGroup();
        //创建客户端的引导对象
        Bootstrap bootstrap = new Bootstrap();
        //配置启动引导对象
        bootstrap.group(group)
                //设置通道为 NIO
                .channel(NioSocketChannel.class)
                //设置请求协议为TCP
                .option(ChannelOption.TCP_NODELAY, true)
                //监听channel并初始化
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //获取ChannelPipeline
                        ChannelPipeline pipeline = ch.pipeline();
                        //设置编码
                        pipeline.addLast(new StringEncoder());
                        pipeline.addLast(new StringDecoder());
                        //添加自定义事件处理器
                        //pipeline.addLast(rpcDemoClientHandler);
                        pipeline.addLast(handlerAdapter);
                    }
                });
        //连接服务端
        bootstrap.connect("127.0.0.1", 30288).sync();

    }

    /**
     * 使用JDK的动态代理创建对象
     * @param serviceClass  接口类型，根据哪个接口生成子类代理对象
     * @param providerParam 字符串 "RpcDemoService#sendRpcRequest#"
     * @return
     */
    public static Object createProxy(Class<?> serviceClass, String providerParam) {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{serviceClass}, (proxy, method, args) -> {
                    //1.初始化客户端client
                    if (rpcDemoClientHandler == null) {
                        initClient(RpcDemoClientHandler.class);
                        rpcDemoClientHandler = (RpcDemoClientHandler) handlerAdapter;
                    }
                    //2.给rpcDemoClientHandler设置参数
                    rpcDemoClientHandler.setParam(providerParam + "are you ok?");
                    //3.使用线程池，开启一个线程处理call()写操作，并返回结果
                    @SuppressWarnings("unchecked")
                    Object result = executorService.submit(rpcDemoClientHandler).get();
                    //4.return结果
                    return result;
                });
    }

    public static Object createReqProxy(Class<?> serviceClass, String providerParam) {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{serviceClass}, (proxy, method, args) -> {
                    //1.初始化客户端client
                    if (rpcReqClientHandler == null) {
                        initClient(RpcReqClientHandler.class);
                        rpcReqClientHandler = (RpcReqClientHandler) handlerAdapter;
                    }
                    //2.给rpcDemoClientHandler设置参数
                    rpcReqClientHandler.setParam(args[0]);
                    //3.使用线程池，开启一个线程处理call()写操作，并返回结果
                    @SuppressWarnings("unchecked")
                    Object result = executorService.submit(rpcReqClientHandler).get();
                    //4.return结果
                    return result;
                });
    }
}
