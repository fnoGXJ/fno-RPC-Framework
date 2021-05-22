package com.fno.rpc.test.netty;

import com.fno.rpc.telecommunication.RpcServer;
import com.fno.rpc.annotation.ServiceScan;
import com.fno.rpc.telecommunication.netty.server.NettyServer;

@ServiceScan
public class TestNettyServer {
    public static void main(String[] args) {
        RpcServer server = new NettyServer(8885);
        server.start();
    }
}
