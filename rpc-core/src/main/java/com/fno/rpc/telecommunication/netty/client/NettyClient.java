package com.fno.rpc.telecommunication.netty.client;

import com.fno.config.ClientConfiguration;
import com.fno.rpc.telecommunication.AbstractRpcClient;
import com.fno.rpc.telecommunication.RpcClient;
import com.fno.rpc.balance.LoadBalance;
import com.fno.rpc.balance.RandomLoadBalance;
import com.fno.rpc.codec.CommonDecoder;
import com.fno.rpc.codec.CommonEncoder;
import com.fno.rpc.entity.RpcRequest;
import com.fno.rpc.entity.RpcResponse;
import com.fno.rpc.enumeration.SerializerCode;
import com.fno.rpc.telecommunication.netty.client.handler.NettyClientHandler;
import com.fno.rpc.registry.NacosServiceRegistry;
import com.fno.rpc.registry.ServiceRegistry;
import com.fno.rpc.serializer.Serializer;
import com.fno.rpc.utils.RpcMessageUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

@Slf4j
public class NettyClient extends AbstractRpcClient {
    private static final Bootstrap bootstrap;

    static {
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new CommonEncoder(configuration.getSerializer()));
                        pipeline.addLast(new CommonDecoder());
                        pipeline.addLast(new NettyClientHandler());
                    }
                });
    }

    public NettyClient() {
        load();
        configuration.getLoadBalance();
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        InetSocketAddress serviceAddress = serviceRegistry.findServiceAddress(rpcRequest.getInterfaceName(), configuration.getLoadBalance());
        try {
            final ChannelFuture future = bootstrap.connect(serviceAddress).sync();
            log.info("已连接到服务器 host:{}, port:{}", serviceAddress.getHostName(), serviceAddress.getPort());
            Channel channel = future.channel();
            if (channel != null) {
                channel.writeAndFlush(rpcRequest).addListener(writeFuture -> {
                    if (writeFuture.isSuccess()) {
                        log.info("客户端发送消息：{}", rpcRequest.toString());
                    } else {
                        log.error("服务端接收消息失败...", future.cause());
                    }
                });
                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse" + rpcRequest.getRequestId());
                RpcResponse rpcResponse = channel.attr(key).get();
                RpcMessageUtils.check(rpcRequest, rpcResponse);
                return rpcResponse.getData();
            }

        } catch (InterruptedException e) {
            log.error("服务器连接客户端时有错误...", e);
        }
        return null;
    }
}
