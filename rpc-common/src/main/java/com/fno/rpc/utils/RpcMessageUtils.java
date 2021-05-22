package com.fno.rpc.utils;

import com.fno.rpc.entity.RpcRequest;
import com.fno.rpc.entity.RpcResponse;
import com.fno.rpc.enumeration.ResponseCode;
import com.fno.rpc.enumeration.RpcError;
import com.fno.rpc.exception.RpcException;

/*
 * 检测RpcRequest与RpcResponse匹配性
 *
 */

public class RpcMessageUtils {
    public static void check(RpcRequest rpcRequest, RpcResponse rpcResponse) {
        if (rpcResponse == null) {
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, "serviceName:" + rpcRequest.getInterfaceName());
        }
        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcError.RESPONSE_NOT_MATCH, "serviceName:" + rpcRequest.getInterfaceName());
        }
        if (rpcResponse.getStatusCode() == null || !rpcResponse.getStatusCode().equals(ResponseCode.SUCCESS.getCode())) {
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, "serviceName:" + rpcRequest.getInterfaceName());
        }
    }
}
