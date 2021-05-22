package com.fno.rpc.telecommunication.socket.client;

import com.fno.rpc.balance.LoadBalance;
import com.fno.rpc.balance.RandomLoadBalance;
import com.fno.rpc.enumeration.SerializerCode;
import com.fno.rpc.registry.NacosServiceRegistry;
import com.fno.rpc.registry.ServiceRegistry;
import com.fno.rpc.serializer.Serializer;
import com.fno.rpc.telecommunication.RpcClient;
import com.fno.rpc.entity.RpcRequest;
import com.fno.rpc.entity.RpcResponse;
import com.fno.rpc.telecommunication.socket.utils.ObjectReader;
import com.fno.rpc.telecommunication.socket.utils.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);
    private final Serializer serializer;
    private final ServiceRegistry serviceRegistry;

    public SocketClient() {
        this(SerializerCode.KRYO);
    }

    public SocketClient(SerializerCode code) {
        this(code, new RandomLoadBalance());
    }

    public SocketClient(LoadBalance loadBalance) {
        this(SerializerCode.KRYO, loadBalance);
    }

    public SocketClient(SerializerCode code, LoadBalance loadBalance) {
        if (loadBalance == null) loadBalance = new RandomLoadBalance();
        this.serializer = Serializer.getSerializerByCode(code.getCode());
        this.serviceRegistry = new NacosServiceRegistry(loadBalance);
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        InetSocketAddress serviceAddress = serviceRegistry.findServiceAddress(rpcRequest.getInterfaceName());
        try (Socket socket = new Socket(serviceAddress.getHostName(), serviceAddress.getPort())) {
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            ObjectWriter.writeObject(outputStream, rpcRequest, serializer);
            return ((RpcResponse) ObjectReader.readObject(inputStream)).getData();
        } catch (IOException e) {
            logger.error("向远程请求时发生错误: ", e);
            return null;
        }
    }
}
