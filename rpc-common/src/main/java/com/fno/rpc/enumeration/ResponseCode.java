package com.fno.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 枚举类，定义了返回的结果（状态码）
 *H
 */
@Getter
@AllArgsConstructor
public enum ResponseCode {
    SUCCESS(200, "调用方法成功"),
    FAIL(400, "调用方法失败"),
    NO_SUCH_METHOD(500,"没有对应的方法");
    private final Integer code;
    private final String message;
}
