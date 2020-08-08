## 分布式架构网络通信
在分布式服务框架中，一个最基础的问题就是远程服务是怎么通讯的，在Java领域中有很多可实现远程通讯的技术，例如：RMI、Hessian、SOAP、ESB和JMS等，它们背后到底是基于什么原理实现的呢
### 1. 基本原理
------

要实现网络机器间的通讯，首先得来看看计算机系统网络通信的基本原理，在底层层面去看，网络通信需要做的就是将流从一台计算机传输到另外一台计算机，基于传输协议和网络IO来实现，其中传输协议比较出名的有tcp、udp等等，tcp、udp都是在基于Socket概念上为某类应用场景而扩展出的传输协议，网络IO，主要有bio、nio、aio三种方式，所有的分布式应用通讯都基于这个原理而实现，只是为了应用的易用，各种语言通常都会提供一些更为贴近应用易用的应用层协议。

### 2. 什么是RPC
------

RPC全称为remote procedure call，即远程过程调用。
借助RPC可以做到像本地调用一样调用远程服务，是一种进程间的通信方式
比如两台服务器A和B，A服务器上部署一个应用，B服务器上部署一个应用，A服务器上的应用想调用B服务器上的应用提供的方法，由于两个应用不在一个内存空间，不能直接调用，所以需要通过网络来表达调用的语义和传达调用的数据。
需要注意的是RPC并不是一个具体的技术，而是指整个网络远程调用过程。

**RPC架构**
一个完整的RPC架构里面包含了四个核心的组件，分别是Client，Client Stub，Server以及Server Stub，这个Stub可以理解为存根。
- 客户端(Client)，服务的调用方。
- 客户端存根(Client Stub)，存放服务端的地址消息，再将客户端的请求参数打包成网络消息，然后通过网络远程发送给服务方。
- 服务端(Server)，真正的服务提供者。
- 服务端存根(Server Stub)，接收客户端发送过来的消息，将消息解包，并调用本地的方法。

**RPC调用过程**

- 1、客户端（client）以本地调用方式（ 即以接口的方式）调用服务；
- 2、客户端存根（client stub）接收到调用后，负责将方法、参数等组装成能够进行网络传输的消息体（将消息体对象序列化为二进制）；
- 3、客户端通过sockets将消息发送到服务端；
- 4、服务端存根( server stub）收到消息后进行解码（将消息对象反序列化）；
- 5、服务端存根( server stub）根据解码结果调用本地的服务；
- 6、本地服务执行并将结果返回给服务端存根( server stub）；
- 7、服务端存根( server stub）将返回结果打包成消息（将结果消息对象序列化）；
- 8、服务端（server）通过sockets将消息发送到客户端；
- 9、客户端存根（client stub）接收到结果消息，并进行解码（将结果消息发序列化）；
- 10、客户端（client）得到最终结果。

RPC的目标是要把2、3、4、7、8、9这些步骤都封装起来。

**注意：**无论是何种类型的数据，最终都需要转换成二进制流在网络上进行传输，数据的发送方需要将对象转换为二进制流，而数据的接收方则需要把二进制流再恢复为对象。

在java中RPC框架比较多，常见的有Hessian、gRPC、Thrift、HSF (High Speed Service Framework)、Dubbo等，其实对 于RPC框架而言，核心模块 就是通讯和序列化

### 3. RMI
------

Java RMI 指的是远程方法调用 (Remote Method Invocation),是java原生支持的远程调用 ,采用JRMP（Java Remote Messageing protocol）作为通信协议，可以认为是纯java版本的分布式远程调用解决方案， RMI主要用于不同虚拟机之间的通信，这些虚拟机可以在不同的主机上、也可以在同一个主机上，这里的通信可以理解为一个虚拟机上的对象调用另一个虚拟机上对象的方法。
##### 1. 客户端
 1）存根/桩(Stub)：远程对象在客户端上的代理；         
 2）远程引用层(Remote Reference Layer)：解析并执行远程引用协议；         
 3）传输层(Transport)：发送调用、传递远程方法参数、接收远程方法执行结果。
##### 2. 服务端
 1）骨架(Skeleton)：读取客户端传递的方法参数，调用服务器方的实际对象方法，并接收方法执行后的返回值；
 2）远程引用层(Remote Reference Layer)：处理远程引用后向骨架发送远程方法调用；
 3）传输层(Transport)：监听客户端的入站连接，接收并转发调用到远程引用层。
##### 3. 注册表(Registry)：以URL形式注册远程对象，并向客户端回复对远程对象的引用。
**远程调用过程** 

> 1）客户端从远程服务器的注册表中查询并获取远程对象引用。 
>
> 2）桩对象与远程对象具有相同的接口和方法列表，当客户端调用远程对象时，实际上是由相应的桩对象代理完成的。 
>
> 3)   远程引用层在将桩的本地引用转换为服务器上对象的远程引用后，再将调用传递给传输层(Transport)，由传输层通 过TCP协议发送调用； 
>
> 4）在服务器端，传输层监听入站连接，它一旦接收到客户端远程调用后，就将这个引用转发给其上层的远程引用层； 
>
> 5）服务器端的远程引用层将客户端发送的远程应用转换为本地虚拟机的引用后，再将请求传递给骨架(Skeleton)； 
>
> 6）骨架读取参数，又将请求传递给服务器，最后由服务器进行实际的方法调用。 

**结果返回过程**

>1) 如果远程方法调用后有返回值，则服务器将这些结果又沿着 “骨架 -> 远程引用层 -> 传输层” 向下传递； 
> 
> 2) 客户端的传输层接收到返回值后，又沿着 “传输层 -> 远程引用层 -> 桩” 向上传递，然后由桩来反序列化这些返回值，并 将最终的结果传递给客户端程序。

### 4. BIO、NIO、AIO
------
#### 同步和异步
同步（synchronize）、异步（asychronize）是指应用程序和内核的交互而言的.

**同步：**
指用户进程触发IO操作等待或者轮训的方式查看IO操作是否就绪。

**异步：**
当一个异步进程调用发出之后，调用者不会立刻得到结果。而是在调用发出之后，被调用者通过状态、通知来通知调用者，或者通过回调函数来处理这个调用。
使用异步IO时，Java将IO读写委托给OS处理，需要将数据缓冲区地址和大小传给OS，OS需要支持异步IO操作

#### 阻塞和非阻塞
阻塞和非阻塞是针对于进程访问数据的时候,根据IO操作的就绪状态来采取不同的方式.
简单点说就是一种读写操作方法的实现方式. 阻塞方式下读取和写入将一直等待, 而非阻塞方式下,读取和写入方法会理解返回一个状态值.

**阻塞：**
使用阻塞IO的时候，Java调用会一直阻塞到读写完成才返回。

**非阻塞：**
使用非阻塞IO时，如果不能读写Java调用会马上返回，当IO事件分发器会通知可读写时再继续进行
读写，不断循环直到读写完成

#### BIO
同步阻塞 IO，B代表blocking
服务器实现模式为一个连接一个线程，即客户端有连接请求时服务器端就需要启动一个线程进行处理，如果这个连接不做任何事情会造成不必要的线程开销，当然可以通过线程池机制改善。
适用场景：Java1.4之前唯一的选择，简单易用但资源开销太高

![image-20200808154426409](C:\Users\cmcc\AppData\Roaming\Typora\typora-user-images\image-20200808154426409.png)

#### NIO
##### 1. NIO介绍
同步非阻塞IO （non-blocking IO / new io）是指JDK 1.4 及以上版本。
服务器实现模式为一个请求一个通道，即客户端发送的连接请求都会注册到多路复用器上，多路复用器轮询到连接有IO请求时才启动一个线程进行处理。

**通道（Channels）**
NIO 新引入的最重要的抽象是通道的概念。Channel 数据连接的通道。 数据可以从Channel读到Buffer中，也可以从Buffer 写到Channel中 

**缓冲区（Buffers）**
通道channel可以向缓冲区Buffer中写数据，也可以像buffer中存数据。

**选择器（Selector）**
使用选择器，借助单一线程，就可对数量庞大的活动 I/O 通道实时监控和维护。

![image-20200808154346237](C:\Users\cmcc\AppData\Roaming\Typora\typora-user-images\image-20200808154346237.png)

##### 2. 特点
当一个连接创建后，不会需要对应一个线程，这个连接会被注册到多路复用器，所以一个连接只需要一个线程即可，所有的连接需要一个线程就可以操作，该线程的多路复用器会轮训，发现连接有请求时，才开启一个线程处理。

IO模型中，一个连接来了，会创建一个线程，对应一个while死循环，死循环的目的就是不断监测这
条连接上是否有数据可以读，大多数情况下，1w个连接里面同一时刻只有少量的连接有数据可读，因此，很多个while死循环都白白浪费掉了，因为读不出啥数据。

在NIO模型中，他把这么多while死循环变成一个死循环，这个死循环由一个线程控制，那么他又是如何做到一个线程，一个while死循环就能监测1w个连接是否有数据可读的呢？ 这就是NIO模型中selector的作用，一条连接来了之后，现在不创建一个while死循环去监听是否有数据可读了，而是直接把这条连接注册到selector上，然后，通过检查这个selector，就可以批量监测出有数据可读的连接，进而读取数据

#### AIO
异步非阻塞IO。A代表asynchronize

当有流可以读时,操作系统会将可以读的流传入read方法的缓冲区,并通知应用程序,对于写操作,OS将write方法的流写入完毕是操作系统会主动通知应用程序。因此read和write都是异步 的，完成后会调用回调函数。

使用场景：连接数目多且连接比较长（重操作）的架构，比如相册服务器。重点调用了OS参与并发操作，编程比较复杂。Java1.7开始支持

### 5. Netty 和基于 Netty 自定义分布式 RPC
------
#### 5.1 Netty 简介
Netty 是由 JBOSS 提供一个异步的、 基于事件驱动的网络编程框架。

Netty 可以帮助你快速、 简单的开发出一 个网络应用， 相当于简化和流程化了 NIO 的开发过程。 作为当前最流行的 NIO 框架， Netty 在互联网领域、 大数据分布式计算领域、 游戏行业、 通信行业等获得了广泛的应用， 知名的 Elasticsearch 、 Dubbo 框架内部都采用了 Netty。

##### 为什么使用Netty
**NIO缺点**
- NIO 的类库和 API 繁杂，使用麻烦。你需要熟练掌握 Selector、ServerSocketChannel、SocketChannel、ByteBuffer 等.
- 可靠性不强，开发工作量和难度都非常大
- NIO 的 Bug。例如 Epoll Bug，它会导致 Selector 空轮询，最终导致 CPU 100%。
- 
**Netty优点**
- 对各种传输协议提供统一的 API
- 高度可定制的线程模型——单线程、一个或多个线程池
- 更好的吞吐量，更低的等待延迟
- 更少的资源消耗
- 最小化不必要的内存拷贝

使用Netty之后，一方面Netty对NIO封装得如此完美，写出来的代码非常优雅，另外一方面，使用Netty之后，网

络通信这块的性能问题几乎不用操心

#### 5.2 Netty 线程模型
##### 5.2.1 单线程模型

![image-20200808153536337](C:\Users\cmcc\AppData\Roaming\Typora\typora-user-images\image-20200808153536337.png)

##### 5.2.2 线程池模型

![image-20200808153642979](C:\Users\cmcc\AppData\Roaming\Typora\typora-user-images\image-20200808153642979.png)

##### 5.2.3 Netty模型

![image-20200808153739080](C:\Users\cmcc\AppData\Roaming\Typora\typora-user-images\image-20200808153739080.png)

Netty 抽象出两组线程池， BossGroup 专门负责接收客 户端连接， WorkerGroup 专门负责网络读写操作。

NioEventLoop 表示一个不断循环执行处理 任务的线程， 每个 NioEventLoop 都有一个 selector， 用于监听绑定

在其上的 socket 网络通道。 NioEventLoop 内部采用串行化设计， 从消息的读取->解码->处理->编码->发送， 始

终由 IO 线 程 NioEventLoop 负责。

#### 5.3 Netty 核心组件

**ChannelHandler** **及其实现类**

ChannelHandler 接口定义了许多事件处理的方法， 我们可以通过重写这些方法去实现具 体的业务逻辑

我们经常需要自定义一个 Handler 类去继承 ChannelInboundHandlerAdapter， 然后通过 重写相应方法实现业

务逻辑， 我们接下来看看一般都需要重写哪些方法：

```java
//通道就绪事件 
public void channelActive(ChannelHandlerContext ctx);	

//通道读取数据事件 
public void channelRead(ChannelHandlerContext ctx, Object msg);	
    
//数据读取完毕事件 
public void channelReadComplete(ChannelHandlerContext ctx);	
    
//通道发生异常事件
public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause);
```



**ChannelPipeline**

ChannelPipeline 是一个 Handler 的集合， 它负责处理和拦截 inbound 或者 outbound 的事 件和操作， 相当于

一个贯穿 Netty 的链。

```java
//把一个业务处理类(handler)添加到链中的第一个位置 
ChannelPipeline addFirst(ChannelHandler... handlers);
    
//把一个业务处理类(handler)添加到链中的最后一个位置
ChannelPipeline addLast(ChannelHandler... handlers);
```



**ChannelHandlerContext**

这 是 事 件 处 理 器 上 下 文 对 象 ， Pipeline 链 中 的 实 际 处 理 节 点 。 每 个 处 理 节 点ChannelHandlerContext 中 包 含 一 个 具 体 的 事 件 处 理 器 ChannelHandler ， 同 ChannelHandlerContext 中也绑定了对应的 pipeline 和 Channel 的信息，方便对 ChannelHandler 进行调用。

常用方法如下所示

```java
//关闭通道 
ChannelFuture close();

//刷新
ChannelOutboundInvoker flush();  

//将数据写到ChannelPipeline中当前ChannelHandle的下一个ChannelHandler开始处理(出栈)
ChannelFuture writeAndFlush(Object msg);
```



**ChannelFuture**

表示 Channel 中异步 I/O 操作的结果， 在 Netty 中所有的 I/O 操作都是异步的， I/O 的调 用会直接返回， 调用者并不能立刻获得结果， 但是可以通过 ChannelFuture 来获取 I/O 操作 的处理状态。 常用方法如下所示：

```java
//返回当前正在进行 IO 操作的通道 
Channel channel();

//等待异步操作执行完毕
ChannelFuture sync();
```



**EventLoopGroup** **和其实现类** **NioEventLoopGroup**

EventLoopGroup 是一组 EventLoop 的抽象， Netty 为了更好的利用多核 CPU 资源， 一般 会有多个 EventLoop同时工作， 每个 EventLoop 维护着一个 Selector 实例。 EventLoopGroup 提供 next 接口， 可以从组里面按照一定规则获取其中一个 EventLoop 来处理任务。 在 Netty 服务器端编程中， 我们一般都需要提供两个EventLoopGroup， 例如： BossEventLoopGroup 和 WorkerEventLoopGroup。 

```java
// 构造方法 
public NioEventLoopGroup();
//断开连接， 关闭线程
public Future<?> shutdownGracefully();
```

 

**ServerBootstrap** **和** **Bootstrap**

ServerBootstrap 是 Netty 中的服务器端启动助手，通过它可以完成服务器端的各种配置； Bootstrap 是 Netty 中的客户端启动助手， 通过它可以完成客户端的各种配置。 

常用方法如下所示：

```java
//该方法用于服务器端，用来设置两个EventLoop 
public ServerBootstrap group(EventLoopGroup parentGroup, EventLoopGroup childGroup);

//该方法用于客户端，用来设置一个 EventLoop 
public B group(EventLoopGroup group); 

//该方法用来设置一个服务器端的通道实现
public B channel(Class<? extends C> channelClass);  

//用来给 ServerChannel 添加配置 
public <T> B option(ChannelOption<T> option, T value);

//用来给接收到的通道添加配置 
public <T> ServerBootstrap childOption(ChannelOption<T> childOption, T value);

//该方法用来设置业务处理类（自定义的handler） 
public ServerBootstrap childHandler(ChannelHandler childHandler);

//该方法用于服务器端，用来设置占用的端口号 
public ChannelFuture bind(int inetPort);

//该方法用于客户端，用来连接服务器端
public ChannelFuture connect(String inetHost, int inetPort);
```



#### 5.4 基于Netty自定义RPC
RPC又称远程过程调用，我们所知的远程调用分为两种，现在在服务间通信的方式也基本以这两种为主

- 是基于HTTP的restful形式的广义远程调用，以spring could的feign和restTemplate为代表，采用的协议是HTTP的7层调用协议，并且协议的参数和响应序列化基本以JSON格式和XML格式为主。

- 是基于TCP的狭义的RPC远程调用，以阿里的Dubbo为代表，主要通过netty来实现4层网络协议，NIO来异步传输，序列化也可以是JSON或者hessian2以及java自带的序列化等，可以配置。


