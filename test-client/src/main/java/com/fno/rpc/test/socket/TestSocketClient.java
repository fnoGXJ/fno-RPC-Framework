package com.fno.rpc.test.socket;

import com.fno.rpc.RpcClient;
import com.fno.rpc.api.HelloObject;
import com.fno.rpc.api.HelloService;
import com.fno.rpc.RpcClientProxy;
import com.fno.rpc.socket.client.SocketClient;

public class TestSocketClient {
    public static void main(String[] args) {
        RpcClient client = new SocketClient("localhost",8884);
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService service = proxy.getProxy(HelloService.class);
        HelloObject helloObject = new HelloObject(12, "我是一条helloObject消息......");
        String hello = service.hello(helloObject);
        System.out.println(hello);
    }
}
