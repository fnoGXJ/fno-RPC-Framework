package com.fno.rpc;

import com.fno.rpc.enumeration.SerializerCode;

public interface RpcServer {
    void start();
    void start(SerializerCode serializerCode);
    <T> void publishService(Object service, Class<T> clazz);
}
