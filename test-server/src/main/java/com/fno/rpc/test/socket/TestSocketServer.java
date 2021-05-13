package com.fno.rpc.test.socket;

import com.fno.rpc.api.HelloService;
import com.fno.rpc.provider.DefaultServiceProvider;
import com.fno.rpc.provider.ServiceProvider;
import com.fno.rpc.RpcServer;
import com.fno.rpc.socket.server.SocketServer;
import com.fno.rpc.test.HelloServiceImpl;

public class TestSocketServer {
    public static void main(String[] args) throws InterruptedException {
        HelloService helloService = new HelloServiceImpl();
        ServiceProvider serviceRegistry = new DefaultServiceProvider();
        serviceRegistry.addService(helloService);
        RpcServer rpcServer = new SocketServer(8884, serviceRegistry);
        rpcServer.start();
    }
}
