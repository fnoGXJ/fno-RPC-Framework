package com.fno.rpc.handler;

import com.fno.rpc.entity.RpcRequest;
import com.fno.rpc.entity.RpcResponse;
import com.fno.rpc.provider.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServiceHandleThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ServiceHandleThread.class);
    private final ServiceProvider serviceProvider;
    private final RequestHandler requestHandler;
    private final Socket socket;

    public ServiceHandleThread(ServiceProvider serviceRegistry, RequestHandler requestHandler, Socket socket) {
        this.serviceProvider = serviceRegistry;
        this.requestHandler = requestHandler;
        this.socket = socket;
    }

    @Override
    public void run() {
        try (ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {
            RpcRequest rpcRequest = (RpcRequest) inputStream.readObject();
            Object result = requestHandler.handle(rpcRequest);
            outputStream.writeObject(RpcResponse.success(result));
            outputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("调用时有错误发生:{}", e);
        }
    }
}
