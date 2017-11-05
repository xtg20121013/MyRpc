package com.xtg.rpc.handler;

import com.xtg.rpc.protocol.Message;
import com.xtg.rpc.serialize.HessianSerializeUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcOutEncoder extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(msg instanceof Message) {
            log.info("【序列化】将message转化为字节流，发送到channel传输，{}", msg);
            ByteBuf byteBuf = convertObj2Buf(msg);
            ctx.write(byteBuf, promise);
        }else {
            ctx.write(msg, promise);
        }
    }

    public ByteBuf convertObj2Buf(Object obj){
        byte[] bytes = HessianSerializeUtils.serialize(obj);
        ByteBuf message = Unpooled.buffer(bytes.length);
        message.writeBytes(bytes);
        return message;
    }

}