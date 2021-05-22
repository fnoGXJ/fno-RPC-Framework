package com.fno.rpc.entity;

import com.fno.rpc.enumeration.ResponseCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 服务器端返回客户端的协议包
 *
 * @author fno
 */
@Data
public class RpcResponse<T> implements Serializable {
    private String requestId;
    /**
     * 返回状态码
     */
    private Integer statusCode;
    /**
     * 返回消息
     */
    private String message;
    /**
     * 返回数据
     */
    private T data;

    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setRequestId(requestId);
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setMessage(ResponseCode.SUCCESS.getMessage());
        response.setData(data);
        return response;
    }

    public static <T> RpcResponse<T> fail(ResponseCode code, String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setRequestId(requestId);
        response.setStatusCode(code.getCode());
        response.setMessage(code.getMessage());
        return response;
    }
}
