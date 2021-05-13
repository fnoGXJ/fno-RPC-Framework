package com.fno.rpc.test.netty;

import com.fno.rpc.RpcClient;
import com.fno.rpc.RpcClientProxy;
import com.fno.rpc.api.TestService;
import com.fno.rpc.netty.client.NettyClient;

public class TestNettyClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        TestService proxy = rpcClientProxy.getProxy(TestService.class);
        String wtf = proxy.justTest("what");
        System.out.println(wtf);
    }
}
