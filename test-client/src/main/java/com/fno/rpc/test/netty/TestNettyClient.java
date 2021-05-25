package com.fno.rpc.test.netty;

import com.fno.rpc.annotation.ClientLoadBalance;
import com.fno.rpc.telecommunication.RpcClient;
import com.fno.rpc.telecommunication.RpcClientProxy;
import com.fno.rpc.api.HelloObject;
import com.fno.rpc.api.HelloService;
import com.fno.rpc.telecommunication.netty.client.NettyClient;
@ClientLoadBalance(name = "consistentHash")
public class TestNettyClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService proxy = rpcClientProxy.getProxy(HelloService.class);
        String wtf = proxy.hello(new HelloObject(11, "我可以做你的爸爸吗"));
        System.out.println(wtf);
    }
}
