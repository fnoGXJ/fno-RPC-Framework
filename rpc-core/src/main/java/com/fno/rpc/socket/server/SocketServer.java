package com.fno.rpc.socket.server;

import com.fno.rpc.enumeration.SerializerCode;
import com.fno.rpc.provider.ServiceProvider;
import com.fno.rpc.handler.RequestHandler;
import com.fno.rpc.RpcServer;
import com.fno.rpc.handler.ServiceHandleThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class SocketServer implements RpcServer {
    private final ExecutorService threadPool;
    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);
    private final int port;
    private final ServiceProvider service;
    private final RequestHandler handler;

    public SocketServer(int port, ServiceProvider service) {
        this.port = port;
        this.service = service;
        handler = new RequestHandler();
        int corePoolSize = 5;
        int maximumPoolSize = 100;
        long keepAliveTime = 60;
        BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, blockingQueue, threadFactory);
    }

    @Override
    public void start() {
        this.start(SerializerCode.KRYO);
    }

    @Override
    public void start(SerializerCode serializerCode) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("服务器启动...");
            logger.info("等待客户端连接中...");
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                logger.info("客户端连接成功，连接IP: " + socket.getInetAddress() + "端口号:" + socket.getPort());
                threadPool.execute(new ServiceHandleThread(service, handler, socket));
            }
        } catch (IOException e) {
            logger.error("服务器启动失败", e);
        }
    }

    @Override
    public <T> void publishService(Object service, Class<T> clazz) {

    }
}
