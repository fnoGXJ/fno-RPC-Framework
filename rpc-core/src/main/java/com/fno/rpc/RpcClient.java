package com.fno.rpc;

import com.fno.rpc.entity.RpcRequest;

public interface RpcClient {
    Object sendRequest(RpcRequest rpcRequest) throws InterruptedException;
}
