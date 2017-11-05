package com.xtg.rpc.handler;

import com.xtg.rpc.protocol.Header;
import com.xtg.rpc.protocol.Invoker;
import com.xtg.rpc.protocol.Message;
import com.xtg.rpc.protocol.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcInHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof Message) {
            Message message = (Message) msg;
            Header header = message.getHeader();
            Invoker invoker = null;
            if(header.getType() == MessageType.REQUEST){
                log.info("【解析协议消息】解析message,发现是client发来的request请求，{}", message);
                invoker = buildRequestInvoker(message);
                log.info("【解析协议消息】将message转化为invoker,{}", invoker);
            }
            if(header.getType() == MessageType.RESPONSE){
                log.info("【解析协议消息】解析message,发现是server响应的response，{}", message);
                invoker = buildResponseInvoker(message);
                log.info("【解析协议消息】将message转化为invoker,{}", invoker);
            }
            ctx.fireChannelRead(invoker);
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    public Invoker buildRequestInvoker(Message message){
        Header header = message.getHeader();
        Invoker invoker = new Invoker();
        invoker.setRequestId(header.getRequestId());
        invoker.setService(header.getService());
        invoker.setMethod(header.getMethod());
        invoker.setMessageType(MessageType.REQUEST);
        if(message.getBody() != null){
            invoker.setRequestParams((Object[])message.getBody());
        }
        return invoker;
    }

    public Invoker buildResponseInvoker(Message message){
        Header header = message.getHeader();
        Invoker invoker = new Invoker();
        invoker.setRequestId(header.getRequestId());
        invoker.setService(header.getService());
        invoker.setMethod(header.getMethod());
        invoker.setMessageType(MessageType.RESPONSE);
        if(message.getBody() != null){
            invoker.setResponseResult(message.getBody());
        }
        return invoker;
    }
}
