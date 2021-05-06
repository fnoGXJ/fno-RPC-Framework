package com.fno.rpc.server;

import com.fno.rpc.entity.RpcRequest;
import com.fno.rpc.entity.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class ServerThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ServerThread.class);
    private Socket socket;
    private Object service;

    public ServerThread(Socket socket, Object service) {
        this.socket = socket;
        this.service = service;
    }

    @Override
    public void run() {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
            RpcRequest rpcRequest = (RpcRequest) inputStream.readObject();
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            Object returnObj = method.invoke(service, rpcRequest.getParameters());
            outputStream.writeObject(RpcResponse.success(returnObj));
            outputStream.flush();
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error("调用时有错误发生", e);
        }
    }
}
