package com.xtg.rpc.client;

import com.xtg.rpc.handler.*;
import com.xtg.rpc.protocol.Invoker;
import com.xtg.rpc.protocol.MessageType;
import com.xtg.rpc.server.MyRpcServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class MyRpcClient {

    public void connect(String host, int port){
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //读
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
                            ch.pipeline().addLast(new RpcInDecoder());
                            ch.pipeline().addLast(new RpcInHandler());
                            ch.pipeline().addLast(new RpcInInvoker());
                            //写
                            ch.pipeline().addLast(new LengthFieldPrepender(2));
                            ch.pipeline().addLast(new RpcOutEncoder());
                            ch.pipeline().addLast(new RpcOutHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect(host, port).sync();
            future.channel().writeAndFlush(send());
            future.channel().closeFuture().sync();
        }catch (Exception e){
            log.warn("error!", e);
        }finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(MyRpcServer.class, args);
        new MyRpcClient().connect("localhost", 8080);
    }

    public static Invoker send(){
        Invoker invoker = new Invoker();
        invoker.setMessageType(MessageType.REQUEST);
        invoker.setRequestParams(new Object[]{"艾弗", 100});
        // TODO: 2017/10/30  下面的可以通过反射获得，这里简单写了
        invoker.setService("com.xtg.rpc.server.service.MyService");
        invoker.setMethod("hello");
        return invoker;
    }
}
