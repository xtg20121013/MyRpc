package com.xtg.rpc.handler;

import com.xtg.rpc.protocol.Message;
import com.xtg.rpc.serialize.HessianSerializeUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcInDecoder extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf) msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            Message message = (Message) HessianSerializeUtils.deserialize(bytes);
            log.info("receive a new message: {}", message);
            ctx.fireChannelRead(message);
        }else {
            ctx.fireChannelRead(msg);
        }
    }
}
