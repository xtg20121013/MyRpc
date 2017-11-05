package com.xtg.rpc.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LengthFieldDecoder extends LengthFieldBasedFrameDecoder {

    public LengthFieldDecoder(){
        super(65535, 0, 2, 0, 2);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        log.info("【拆包粘包处理->读】解析报文前两位，得到报文长度，根据该长度读取后面整个报文");
        return super.decode(ctx, in);
    }
}
