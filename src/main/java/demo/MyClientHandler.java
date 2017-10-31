package demo;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MyClientHandler extends ChannelInboundHandlerAdapter {

    public ByteBuf convertString2Buf(String body){
        byte[] bytes = body.getBytes();
        ByteBuf message = Unpooled.buffer(bytes.length);
        message.writeBytes(bytes);
        return message;
    }

    public ByteBuf convertObj2Buf(Object obj){
        byte[] bytes = JSON.toJSONBytes(obj);
        ByteBuf message = Unpooled.buffer(bytes.length);
        message.writeBytes(bytes);
        return message;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 100; i++) {
            UserInfo u = new UserInfo("xtg", "pwd"+i);
            ctx.writeAndFlush(convertObj2Buf(u));
        }
        ctx.fireChannelActive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }
}
