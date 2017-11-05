package com.xtg.rpc.client;

import com.xtg.rpc.protocol.Invoker;
import com.xtg.rpc.protocol.MessageType;
import com.xtg.rpc.support.DefaultFuture;
import com.xtg.rpc.support.ResponseFuture;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ServiceChannelPool {

    private static final Map<String, Channel> CHANNEL_MAP = new ConcurrentHashMap<>();

    public static void register(String serviceName, Channel channel){
        CHANNEL_MAP.put(serviceName, channel);
    }

    public static Object invoke(Invoker invoker){
        String requestId = Invoker.newRequestId();
        invoker.setRequestId(requestId);
        invoker.setMessageType(MessageType.REQUEST);
        String serviceName = invoker.getService();
        if(CHANNEL_MAP.containsKey(serviceName)){
            Channel channel = CHANNEL_MAP.get(serviceName);
            ResponseFuture future = new DefaultFuture(requestId, channel);
            log.info("【服务代理】对该次请求生成requestId={}，并构建response future", requestId);
            channel.writeAndFlush(invoker);
            log.info("【服务代理】准备通过调用future.get()阻塞当前线程，等待结果返回。。。");
            return future.get();
        }else{
            throw new RuntimeException("找不到对应的服务");
        }
    }
}
