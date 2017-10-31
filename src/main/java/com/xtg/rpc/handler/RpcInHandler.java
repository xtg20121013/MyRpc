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
            log.info("receive a new message body: {}", message.getBody());
            Header header = message.getHeader();
            Invoker invoker = null;
            if(header.getType() == MessageType.REQUEST){
                invoker = buildRequestInvoker(message);
            }
            if(header.getType() == MessageType.RESPONSE){
                invoker = buildResponseInvoker(message);
            }
            ctx.fireChannelRead(invoker);
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    public Invoker buildRequestInvoker(Message message){
        Header header = message.getHeader();
        Invoker invoker = new Invoker();
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
        invoker.setService(header.getService());
        invoker.setMethod(header.getMethod());
        invoker.setMessageType(MessageType.RESPONSE);
        if(message.getBody() != null){
            invoker.setResponseResult(message.getBody());
        }
        return invoker;
    }
}
