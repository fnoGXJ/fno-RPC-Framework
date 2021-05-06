package com.fno.rpc.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 客户端发往服务器端的请求协议包
 *
 * @author fno
 */
@Data
@Builder
public class RpcRequest implements Serializable {
    /**
     * 待调用接口名
     */
    private String interfaceName;
    /**
     * 待调用方法名
     */
    private String methodName;
    /**
     * 待调用参数
     */
    private Object[] parameters;
    /**
     * 待调用参数类型
     */
    private Class<?>[] paramTypes;
}
