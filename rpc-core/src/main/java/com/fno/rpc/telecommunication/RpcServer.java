package com.fno.rpc.telecommunication;

import com.fno.rpc.enumeration.SerializerCode;

public interface RpcServer {
    void start();

    <T> void publishService(T service, String serviceName);
}
