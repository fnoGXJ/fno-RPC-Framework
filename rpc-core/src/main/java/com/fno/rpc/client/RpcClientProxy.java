package com.fno.rpc.client;

import com.fno.rpc.entity.RpcRequest;
import com.fno.rpc.entity.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcClientProxy implements InvocationHandler {
    private String host;
    private Integer port;

    public RpcClientProxy(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = RpcRequest.builder()
            .interfaceName(method.getDeclaringClass().getName())
            .methodName(method.getName())
            .parameters(args)
            .paramTypes(method.getParameterTypes())
            .build();
    RpcClient rpcClient = new RpcClient();
        return ((RpcResponse) rpcClient.sendRequest(rpcRequest, host, port)).getData();
}
}
