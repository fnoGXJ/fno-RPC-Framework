package com.fno.rpc.telecommunication.netty.server.handler;

import com.fno.rpc.entity.HeartbeatMsg;
import com.fno.rpc.entity.RpcRequest;
import com.fno.rpc.entity.RpcResponse;
import com.fno.rpc.factory.SingletonFactory;
import com.fno.rpc.handler.RequestHandler;
import com.fno.rpc.provider.DefaultServiceProvider;
import com.fno.rpc.provider.ServiceProvider;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import io.protostuff.Rpc;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private static final RequestHandler requestHandler = SingletonFactory.getInstance(RequestHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof HeartbeatMsg) {
                HeartbeatMsg heartbeatMsg = (HeartbeatMsg) msg;
                log.info(heartbeatMsg.getMessage());
            } else {
                RpcRequest rpcRequest = (RpcRequest) msg;
                log.info("服务器收到请求: {}", rpcRequest);
                Object ret = requestHandler.handle(rpcRequest);
                ctx.writeAndFlush(RpcResponse.success(ret, rpcRequest.getRequestId())).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("no request in 60s, so close the connection");
                ctx.writeAndFlush(new HeartbeatMsg("connection is closed")).addListener(ChannelFutureListener.CLOSE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("过程调用中有错误发生...");
        cause.printStackTrace();
        ctx.close();
    }
}
