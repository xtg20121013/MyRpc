package com.xtg.rpc.handler;

import com.xtg.rpc.protocol.Header;
import com.xtg.rpc.protocol.Invoker;
import com.xtg.rpc.protocol.Message;
import com.xtg.rpc.protocol.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class RpcOutHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        log.info("【构建协议消息】准备发送:{}", msg);
        if(msg instanceof Invoker){
            Invoker invoker = (Invoker) msg;
            Message message = null;
            if(invoker.getMessageType() == MessageType.REQUEST ){
                message = buildRequestMessage(invoker);
                log.info("【构建协议消息】先将请求的invoker转化为message,{}", message);
            }
            if(invoker.getMessageType() == MessageType.RESPONSE){
                message = buildResponseMessage(invoker);
                log.info("【构建协议消息】先将响应的invoker转化为message,{}", message);
            }
            ctx.write(message, promise);
        }else {
            ctx.write(msg, promise);
        }
    }

    public Message buildRequestMessage(Invoker invoker){
        Header header = new Header();
        Message message = new Message();
        long length = 0L;
        if(invoker.getRequestParams() != null){
            message.setBody(invoker.getRequestParams());
        }
        header.setService(invoker.getService());
        header.setMethod(invoker.getMethod());
        header.setType(MessageType.REQUEST);
        header.setLength(length);
        header.setRequestId(invoker.getRequestId());
        message.setHeader(header);
        return message;
    }

    public Message buildResponseMessage(Invoker invoker){
        Header header = new Header();
        Message message = new Message();
        long length = 0L;
        if(invoker.getResponseResult() != null){
            message.setBody(invoker.getResponseResult());
        }
        header.setService(invoker.getService());
        header.setMethod(invoker.getMethod());
        header.setType(MessageType.RESPONSE);
        header.setLength(length);
        header.setRequestId(invoker.getRequestId());
        message.setHeader(header);
        return message;
    }
}
