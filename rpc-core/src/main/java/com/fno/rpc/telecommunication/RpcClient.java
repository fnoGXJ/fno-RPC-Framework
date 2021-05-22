package com.fno.rpc.telecommunication;

import com.fno.rpc.entity.RpcRequest;

public interface RpcClient {
    Object sendRequest(RpcRequest rpcRequest) throws InterruptedException;
}
