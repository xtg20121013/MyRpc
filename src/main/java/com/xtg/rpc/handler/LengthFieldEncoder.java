package com.xtg.rpc.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class LengthFieldEncoder extends LengthFieldPrepender {

    public LengthFieldEncoder(){
        super(2);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        log.info("【拆包粘包处理->写】报文开头保留两位记录报文长度");
        super.encode(ctx, msg, out);
    }
}
