package com.fno.rpc.netty.server;

import com.fno.rpc.RpcServer;
import com.fno.rpc.codec.CommonDecoder;
import com.fno.rpc.codec.CommonEncoder;
import com.fno.rpc.enumeration.SerializerCode;
import com.fno.rpc.netty.server.handler.NettyServerHandler;
import com.fno.rpc.provider.DefaultServiceProvider;
import com.fno.rpc.provider.ServiceProvider;
import com.fno.rpc.registry.NacosServiceRegistry;
import com.fno.rpc.registry.ServiceRegistry;
import com.fno.rpc.serializer.Serializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class NettyServer implements RpcServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private final int port;
    private final ServiceRegistry serviceRegistry;
    private final ServiceProvider serviceProvider;

    public NettyServer(int port) {
        this.port = port;
        this.serviceRegistry = new NacosServiceRegistry();
        this.serviceProvider = new DefaultServiceProvider();
    }

    public void start() {
        this.start(SerializerCode.KRYO);
    }

    public void start(SerializerCode serializerCode) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new CommonEncoder(Serializer.getSerializerByCode(serializerCode.getCode())));
                            pipeline.addLast(new CommonDecoder());
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("服务器启动时有错误发生......", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public <T> void publishService(Object service, Class<T> clazz) {
        serviceProvider.addService(service);
        serviceRegistry.registry(clazz.getCanonicalName(), new InetSocketAddress("localhost", port));
    }
}
