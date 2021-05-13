package com.fno.rpc.netty.client.handler;

import com.fno.rpc.entity.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) throws Exception {
        try {
            logger.info("客户端接收到消息: {}", rpcResponse);
            AttributeKey<RpcResponse> attr = AttributeKey.valueOf("rpcResponse");
            ctx.channel().attr(attr).set(rpcResponse);
            ctx.channel().close();
        } finally {
            ReferenceCountUtil.release(rpcResponse);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("过程调用中有错误发生...");
        cause.printStackTrace();
        ctx.close();
    }
}
