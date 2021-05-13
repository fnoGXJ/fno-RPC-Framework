package com.fno.rpc.exception;

import com.fno.rpc.enumeration.RpcError;

public class RpcException extends RuntimeException {
    public RpcException() {
        super();
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }

    public RpcException(RpcError error) {
        super(error.getMsg());
    }

}
