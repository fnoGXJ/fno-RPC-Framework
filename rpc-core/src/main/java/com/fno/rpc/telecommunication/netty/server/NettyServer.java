package com.fno.rpc.telecommunication.netty.server;

import com.fno.rpc.telecommunication.AbstractRpcServer;
import com.fno.rpc.codec.CommonDecoder;
import com.fno.rpc.codec.CommonEncoder;
import com.fno.rpc.enumeration.SerializerCode;
import com.fno.rpc.telecommunication.netty.server.handler.NettyServerHandler;
import com.fno.rpc.serializer.Serializer;
import com.fno.rpc.hook.ShutdownHook;
import com.fno.rpc.utils.RuntimeUtils;
import com.fno.rpc.utils.current.threadpool.ThreadPoolUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyServer extends AbstractRpcServer {

    public NettyServer(int port) {
        this(port, SerializerCode.KRYO);
    }

    public NettyServer(int port, SerializerCode code) {
        this.port = port;
        this.serializer = Serializer.getSerializerByCode(code.getCode());
        scanServices();
    }

    public void start() {
        ShutdownHook.getShutdownHook().addClearAllHook();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        DefaultEventExecutorGroup serviceHandlerGroup = new DefaultEventExecutorGroup(RuntimeUtils.getCPUs() * 2,
                ThreadPoolUtils.createThreadFactory("service-handler-group", false));
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
                            pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            pipeline.addLast(new CommonEncoder(serializer));
                            pipeline.addLast(new CommonDecoder());
                            pipeline.addLast(serviceHandlerGroup, new NettyServerHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("服务器启动时有错误发生......", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
