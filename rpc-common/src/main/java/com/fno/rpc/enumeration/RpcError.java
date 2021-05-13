package com.fno.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RpcError {
    SERVICE_NOT_FOUND("找不到相应的服务"),
    PACKAGE_TYPE_NOT_DEFINED("无法识别的类型"),
    NOT_RPC_PACKAGE("不是RPC协议包"),
    WRONG_PACKAGE_TYPE("无法识别的类型编号"),
    NO_SUCH_SERIALIZER("无法识别的序列化器"),
    FAIL_CONNET_NACOS("连接nacos出错"),
    FAIL_REGISTRY_SERVICE("向nacos服务注册失败"),
    FAIL_GET_SERVICE("获取服务器实例错误");
    private final String msg;
}
