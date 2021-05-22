package com.fno.rpc.telecommunication.socket.server;

import com.fno.rpc.hook.ShutdownHook;
import com.fno.rpc.provider.DefaultServiceProvider;
import com.fno.rpc.registry.NacosServiceRegistry;
import com.fno.rpc.serializer.Serializer;
import com.fno.rpc.telecommunication.AbstractRpcServer;
import com.fno.rpc.enumeration.SerializerCode;
import com.fno.rpc.handler.RequestHandler;
import com.fno.rpc.handler.ServiceHandleThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class SocketServer extends AbstractRpcServer {
    private final ExecutorService threadPool;
    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);
    private final RequestHandler handler;

    public SocketServer(int port) {
        this(port, SerializerCode.KRYO);
    }

    public SocketServer(int port, SerializerCode code) {
        this.port = port;
        this.serviceRegistry = new NacosServiceRegistry();
        this.serviceProvider = new DefaultServiceProvider();
        handler = new RequestHandler();
        this.serializer = Serializer.getSerializerByCode(code.getCode());
        int corePoolSize = 5;
        int maximumPoolSize = 100;
        long keepAliveTime = 60;
        BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, blockingQueue, threadFactory);
        scanServices();
    }


    @Override
    public void start() {
        ShutdownHook.getShutdownHook().addClearAllHook();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("服务器启动...");
            logger.info("等待客户端连接中...");
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                logger.info("客户端连接成功，连接IP: " + socket.getInetAddress() + "端口号:" + socket.getPort());
                threadPool.execute(new ServiceHandleThread(handler, socket, serializer));
            }
        } catch (IOException e) {
            logger.error("服务器启动失败", e);
        }
    }
}
