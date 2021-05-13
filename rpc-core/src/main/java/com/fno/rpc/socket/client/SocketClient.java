package com.fno.rpc.socket.client;

import com.fno.rpc.RpcClient;
import com.fno.rpc.entity.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);
    private final String host;
    private final int port;

    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
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
