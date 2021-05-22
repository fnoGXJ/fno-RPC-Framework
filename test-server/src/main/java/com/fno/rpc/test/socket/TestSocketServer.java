package com.fno.rpc.test.socket;

import com.fno.rpc.annotation.ServiceScan;
import com.fno.rpc.api.HelloService;
import com.fno.rpc.provider.DefaultServiceProvider;
import com.fno.rpc.provider.ServiceProvider;
import com.fno.rpc.telecommunication.RpcServer;
import com.fno.rpc.telecommunication.socket.server.SocketServer;
import com.fno.rpc.test.netty.serviceImpl.HelloServiceImpl;
@ServiceScan
public class TestSocketServer {
    public static void main(String[] args) throws InterruptedException {
        HelloService helloService = new HelloServiceImpl();
        ServiceProvider serviceRegistry = new DefaultServiceProvider();
        serviceRegistry.addService(helloService);
        RpcServer rpcServer = new SocketServer(8890);
        rpcServer.start();
    }
}
