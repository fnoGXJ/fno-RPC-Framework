package com.fno.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SerializerCode {
    KRYO(1),
    JSON(2);
    private final int code;
}
