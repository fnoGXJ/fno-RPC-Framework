package com.fno.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 枚举类，定义了返回的结果（状态码）
 *
 * @author fno
 */
@Getter
@AllArgsConstructor
public enum ResponseCode {
    SUCCESS(200, "调用方法成功"),
    FAIL(400, "调用方法失败");
    private final Integer code;
    private final String message;
}
