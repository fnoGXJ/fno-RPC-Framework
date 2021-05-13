package com.fno.rpc.netty.server.handler;

import com.fno.rpc.entity.RpcRequest;
import com.fno.rpc.handler.RequestHandler;
import com.fno.rpc.provider.DefaultServiceProvider;
import com.fno.rpc.provider.ServiceProvider;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static final ServiceProvider serviceProvider;
    private static final RequestHandler requestHandler;

    static {
        serviceProvider = new DefaultServiceProvider();
        requestHandler = new RequestHandler();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        try {
            logger.info("服务器收到请求: {}", rpcRequest);
            Object ret = requestHandler.handle(rpcRequest);
            ChannelFuture future = ctx.writeAndFlush(ret);
            future.addListener(ChannelFutureListener.CLOSE);
        } finally {
            ReferenceCountUtil.release(rpcRequest);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("过程调用中有错误发生...");
        cause.printStackTrace();
        ctx.close();
    }
}
