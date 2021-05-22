package com.fno.rpc.test.socket;

import com.fno.rpc.telecommunication.RpcClient;
import com.fno.rpc.api.HelloObject;
import com.fno.rpc.api.HelloService;
import com.fno.rpc.telecommunication.RpcClientProxy;
import com.fno.rpc.telecommunication.socket.client.SocketClient;

public class TestSocketClient {
    public static void main(String[] args) {
        RpcClient client = new SocketClient();
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService service = proxy.getProxy(HelloService.class);
        HelloObject helloObject = new HelloObject(66, "heihei......");
        String hello = service.hello(helloObject);
        System.out.println(hello);
    }
}
