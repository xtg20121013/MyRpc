package com.xtg.rpc.handler;

import com.xtg.rpc.protocol.Invoker;
import com.xtg.rpc.protocol.MessageType;
import com.xtg.rpc.server.service.MyService;
import com.xtg.rpc.server.service.MyServiceImpl;
import com.xtg.rpc.support.DefaultFuture;
import io.netty.channel.Channel;
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
            Invoker invoker = (Invoker) msg;
            if(invoker.getMessageType() == MessageType.REQUEST) {
                log.info("【处理响应】收到来自client的invoker请求,通过线程池处理，{}", invoker);
                this.invoke(ctx.channel(), invoker);
            } else if(invoker.getMessageType() == MessageType.RESPONSE){
                Object result = invoker.getResponseResult();
                log.info("【处理响应】从客户端解析后的带response的invoker中获取返回值: [{}]", result);
                DefaultFuture.received(invoker.getRequestId(), result);
            }
            ctx.fireChannelRead(invoker);
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    public void invoke(final Channel channel, Invoker invoker) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // TODO: 2017/10/30 为了防止阻塞，这个方法里的代码应该丢给线程池去跑,这里为了方便就用线程代替
        new Thread(() -> {
            log.info("【服务调用】通过invoker里的service在beanFactory里找到对应的service实例");
            // TODO: 2017/10/30 这里应该通过service去BeanFactory里找对应service实例，节省时间就不这么写了
            MyService myService = new MyServiceImpl();
            List<Class> clzList = Arrays.stream(invoker.getRequestParams())
                    .map(Object::getClass)
                    .collect(Collectors.toList());
            Class[] clzArray = clzList.toArray(new Class[clzList.size()]);
            log.info("【服务调用】通过invoker里的method以及requestParams反射得到method");
            Object res;
            try {
                Method method = myService.getClass().getDeclaredMethod(invoker.getMethod(), clzArray);
                res = method.invoke(myService, invoker.getRequestParams());
                log.info("【服务调用】调用method，得到返回值,[{}]", res);
            }catch (Exception e){
                log.warn("执行method出错", e);
                res = e.getMessage();
            }
            log.info("【服务调用】往invoker中注入返回值、messageType=RESPONSE");
            invoker.setResponseResult(res);
            invoker.setMessageType(MessageType.RESPONSE);
            log.info("【服务调用】将服务端执行后得到的带response的invoker写入channel，准备返回给客户端，{}", invoker);
            channel.writeAndFlush(invoker);
        }, "<服务端处理服务线程>").start();
    }

}
