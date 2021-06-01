package com.fno.rpc.telecommunication.netty.client.handler;

import com.fno.rpc.entity.HeartbeatMsg;
import com.fno.rpc.entity.RpcResponse;
import com.fno.rpc.factory.SingletonFactory;
import com.fno.rpc.telecommunication.netty.client.NettyClient;
import com.fno.rpc.telecommunication.netty.client.UnprocessedRequests;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private UnprocessedRequests unprocessedRequests;
    private final NettyClient nettyClient;

    public NettyClientHandler(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
        unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) {
        if (obj instanceof HeartbeatMsg) {
            HeartbeatMsg heartbeatMsg = (HeartbeatMsg) obj;
            log.info(heartbeatMsg.getMessage());
        } else {
            RpcResponse rpcResponse = (RpcResponse) obj;
            log.info("client receive msg: [{}]", obj);
            unprocessedRequests.complete(rpcResponse);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("write idle happen [{}]", ctx.channel().remoteAddress());
                ctx.writeAndFlush(new HeartbeatMsg("ping")).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
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
