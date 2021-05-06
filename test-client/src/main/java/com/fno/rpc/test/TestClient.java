package com.fno.rpc.test;

import com.fno.rpc.api.HelloObject;
import com.fno.rpc.api.HelloService;
import com.fno.rpc.client.RpcClientProxy;

public class TestClient {
    public static void main(String[] args) {
        RpcClientProxy proxy = new RpcClientProxy("localhost", 8884);
        HelloService service = proxy.getProxy(HelloService.class);
        HelloObject helloObject = new HelloObject(12, "我是一条helloObject消息......");
        String hello = service.hello(helloObject);
        System.out.println(hello);
    }
}
