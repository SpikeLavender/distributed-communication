package com.natsumes.demo;

import com.natsumes.demo.entity.RequestBody;
import com.natsumes.demo.entity.ResponseBody;
import com.natsumes.demo.handler.RpcReqClientHandler;
import com.natsumes.demo.service.RpcDemoService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;


import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcClient {
    //1.创建一个线程池对象 ---处理我们自定义事件
    private static ExecutorService executorService =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    //2.声明一个自定义事件处理器  RpcDemoClientHandler
    private static RpcReqClientHandler rpcReqClientHandler;

    private static Integer count = 0;

    public static void main(String[] args) throws InterruptedException {
        //1.创建代理对象
        RpcDemoService service = (RpcDemoService) RpcClient.createProxy(RpcDemoService.class);
        //2.循环给服务器写数据
        while (true) {
            count++;
            RequestBody req = new RequestBody();
            req.setId(UUID.randomUUID().toString());
            req.setName(String.valueOf(count));
            req.setReqMsg("数据信息: " + count);

            ResponseBody responseBody = service.sendRpcRequest(req);
            System.out.println(responseBody);
            Thread.sleep(2000);
        }
    }


    //3.编写方法，初始化客户端（创建连接池 bootStrap 设置bootStrap 连接服务器）
    private static void initClient() throws Exception {

        //初始化 RpcDemoClientHandler
        rpcReqClientHandler = new RpcReqClientHandler();

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
                        pipeline.addLast(new ObjectEncoder());
                        pipeline.addLast(new ObjectDecoder(1024* 1024,
                                ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                        //添加自定义事件处理器
                        pipeline.addLast(rpcReqClientHandler);
                    }
                });
        //连接服务端
        bootstrap.connect("127.0.0.1", 30288).sync();

    }

    /**
     * 使用JDK的动态代理创建对象
     * @param serviceClass  接口类型，根据哪个接口生成子类代理对象
     * @return
     */
    public static Object createProxy(Class<?> serviceClass) {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{serviceClass}, (proxy, method, args) -> {
                    //1.初始化客户端client
                    if (rpcReqClientHandler == null) {
                        initClient();
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
