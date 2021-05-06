package com.fno.rpc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class RpcServer {
    private final ExecutorService threadPool;
    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    public RpcServer() {
        int corePoolSize = 5;
        int maximumPoolSize = 100;
        long keepAliveTime = 60;
        BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, blockingQueue, threadFactory);
    }

    public void register(Object service, int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("监听客户端连接中");
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                logger.info("连接成功，连接IP: "+socket.getInetAddress());
                threadPool.execute(new ServerThread(socket, service));
            }
        } catch (IOException e) {
            logger.error("连接时有错误发生", e);
        }
    }
}
