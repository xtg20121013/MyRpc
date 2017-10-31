package com.xtg.rpc.handler;

import com.xtg.rpc.protocol.Invoker;
import com.xtg.rpc.protocol.MessageType;
import com.xtg.rpc.server.service.MyService;
import com.xtg.rpc.server.service.MyServiceImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class RpcInInvoker extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof Invoker) {
            // TODO: 2017/10/30 为了防止阻塞，后面的代码应该丢给线程池去跑
            Invoker invoker = (Invoker) msg;
            log.info("receive a new invoker: {}", invoker);
            // TODO: 2017/10/30 这里应该通过service去BeanFactory里找对应service实例，节省时间就不这么写了
            if(invoker.getMessageType() == MessageType.REQUEST) {
                this.invoke(invoker);
                log.info("service invoked, res: {}", invoker);
                ctx.channel().writeAndFlush(invoker);
            } else if(invoker.getMessageType() == MessageType.RESPONSE){
                log.info("received rpc invoked response {}", invoker.getResponseResult());
            }
            ctx.fireChannelRead(invoker);
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    public Object invoke(Invoker invoker) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MyService myService = new MyServiceImpl();
        List<Class> clzList = Arrays.stream(invoker.getRequestParams())
                .map(Object::getClass)
                .collect(Collectors.toList());
        Class[] clzArray = clzList.toArray(new Class[clzList.size()]);
        Method method = myService.getClass().getDeclaredMethod(invoker.getMethod(), clzArray);
        Object res = method.invoke(myService, invoker.getRequestParams());
        invoker.setResponseResult(res);
        invoker.setMessageType(MessageType.RESPONSE);
        return res;
    }

}
