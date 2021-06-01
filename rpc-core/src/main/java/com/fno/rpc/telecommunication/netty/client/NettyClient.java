package com.fno.rpc.telecommunication.netty.client;

import com.fno.rpc.factory.SingletonFactory;
import com.fno.rpc.telecommunication.AbstractRpcClient;
import com.fno.rpc.codec.CommonDecoder;
import com.fno.rpc.codec.CommonEncoder;
import com.fno.rpc.entity.RpcRequest;
import com.fno.rpc.entity.RpcResponse;
import com.fno.rpc.telecommunication.netty.client.handler.NettyClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyClient extends AbstractRpcClient {
    private final Bootstrap bootstrap;
    private final ChannelProvider channelProvider;
    private final UnprocessedRequests unprocessedRequests;

    public NettyClient() {
        load();
        EventLoopGroup group = new NioEventLoopGroup();
        NettyClient nettyClient = this;
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new IdleStateHandler(0, 30, 0, TimeUnit.SECONDS));
                        pipeline.addLast(new CommonEncoder(configuration.getSerializer()));
                        pipeline.addLast(new CommonDecoder());
                        pipeline.addLast(new NettyClientHandler(nettyClient));
                    }
                });
        channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
        unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @SneakyThrows
    public Channel connect(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("connect success! host:{}, port:{}", inetSocketAddress.getHostName(), inetSocketAddress.getPort());
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalArgumentException();
            }
        });
        return completableFuture.get();
    }

    @Override
    public CompletableFuture<RpcResponse<Object>> sendRequest(RpcRequest rpcRequest) {
        CompletableFuture<RpcResponse<Object>> completeFuture = new CompletableFuture<>();
        InetSocketAddress inetSocketAddress = serviceRegistry.findServiceAddress(rpcRequest.getInterfaceName(), configuration.getLoadBalance());
        Channel channel = getChannel(inetSocketAddress);
        if (channel.isActive()) {
            unprocessedRequests.put(rpcRequest.getRequestId(), completeFuture);
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send request success rpcRequest:{}", rpcRequest.toString());
                } else {
                    future.channel().close();
                    completeFuture.completeExceptionally(future.cause());
                    log.error("Send failed:", future.cause());
                }
            });
        } else {
            throw new IllegalArgumentException();
        }
        return completeFuture;
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.get(inetSocketAddress);
        if (channel == null) {
            channel = connect(inetSocketAddress);
            channelProvider.set(inetSocketAddress, channel);
        }
        return channel;
    }
}
