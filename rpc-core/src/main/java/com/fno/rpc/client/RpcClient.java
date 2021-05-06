package com.fno.rpc.client;

import com.fno.rpc.entity.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RpcClient {
    public static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    public Object sendRequest(RpcRequest rpcRequest, String host, Integer port) {
        try (Socket socket = new Socket(host, port)) {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream.writeObject(rpcRequest);
            outputStream.flush();
            return inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("向远程请求时发生错误: ", e);
            return null;
        }
    }
}
