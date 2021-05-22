package com.fno.rpc.handler;

import com.fno.rpc.entity.RpcRequest;
import com.fno.rpc.entity.RpcResponse;
import com.fno.rpc.provider.ServiceProvider;
import com.fno.rpc.serializer.Serializer;
import com.fno.rpc.telecommunication.socket.utils.ObjectReader;
import com.fno.rpc.telecommunication.socket.utils.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class ServiceHandleThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ServiceHandleThread.class);
    private final RequestHandler requestHandler;
    private final Socket socket;
    private final Serializer serializer;

    public ServiceHandleThread(RequestHandler requestHandler, Socket socket, Serializer serializer) {
        this.requestHandler = requestHandler;
        this.socket = socket;
        this.serializer = serializer;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            RpcRequest rpcRequest = (RpcRequest) ObjectReader.readObject(inputStream);
            Object obj = requestHandler.handle(rpcRequest);
            RpcResponse<Object> rpcResponse = RpcResponse.success(obj, rpcRequest.getRequestId());
            ObjectWriter.writeObject(outputStream, rpcResponse, serializer);
        } catch (IOException e) {
            logger.error("调用时有错误发生:{}", e);
        }
    }
}
