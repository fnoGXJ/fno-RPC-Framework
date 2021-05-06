package com.fno.rpc.test;

import com.fno.rpc.api.HelloService;
import com.fno.rpc.server.RpcServer;

public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        RpcServer rpcServer = new RpcServer();
        rpcServer.register(helloService,8884);
    }
}
