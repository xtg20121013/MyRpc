package com.xtg.rpc.client;

import com.xtg.rpc.handler.*;
import com.xtg.rpc.protocol.Invoker;
import com.xtg.rpc.server.MyRpcServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.CountDownLatch;

@Slf4j
@SpringBootApplication
public class MyRpcClient {

    final static EventLoopGroup group = new NioEventLoopGroup();
    final static String SERVICE_NAME = "com.xtg.rpc.server.service.MyService";
    final static String SERVICE_HOST = "localhost";
    final static int SERVICE_PORT = 8080;

    public static void connect(final String serviceName, final String host, final int port){
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //读
                            ch.pipeline().addLast(new LengthFieldDecoder());
                            ch.pipeline().addLast(new RpcInDecoder());
                            ch.pipeline().addLast(new RpcInHandler());
                            ch.pipeline().addLast(new RpcInInvoker());
                            //写
                            ch.pipeline().addLast(new LengthFieldEncoder());
                            ch.pipeline().addLast(new RpcOutEncoder());
                            ch.pipeline().addLast(new RpcOutHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect(host, port).sync();
            log.info("【服务注册】注册服务{}，注册服务地址{}:{}", serviceName, host, port);
            ServiceChannelPool.register(serviceName, future.channel());
        }catch (Exception e){
            log.warn("error!", e);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(MyRpcServer.class, args);
        MyRpcClient.connect(SERVICE_NAME, SERVICE_HOST, SERVICE_PORT);
        //启动用户线程调用rpc服务，注意观察log中的线程的变化
        new Thread(()->{
            Invoker invoker = new Invoker();
            // TODO: 2017/10/30  下面的可以通过反射获得，这里简单写了
            invoker.setService(SERVICE_NAME);
            invoker.setMethod("hello");
            invoker.setRequestParams(new Object[]{"艾弗", 5});
            log.info("【服务代理】准备开始RPC调用，{}", invoker);
            Object res = ServiceChannelPool.invoke(invoker);
            log.info("【服务代理】RPC调用完成,获得返回结果,[{}]", res);
        }, "<用户调用服务线程>").start();
        //阻塞等待结果
        new CountDownLatch(1).await();
        group.shutdownGracefully();
    }
}
