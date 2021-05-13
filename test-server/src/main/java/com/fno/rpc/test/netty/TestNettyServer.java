package com.fno.rpc.test.netty;

import com.fno.rpc.RpcServer;
import com.fno.rpc.api.HelloService;
import com.fno.rpc.api.TestService;
import com.fno.rpc.netty.server.NettyServer;
import com.fno.rpc.test.HelloServiceImpl;
import com.fno.rpc.test.TestServiceImpl;

public class TestNettyServer {
    public static void main(String[] args) {
        RpcServer server = new NettyServer(8885);
        server.start();
    }
}
