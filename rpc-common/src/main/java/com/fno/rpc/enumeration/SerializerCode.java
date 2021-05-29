package com.fno.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SerializerCode {
    KRYO(1),
    PROTOBUF(2),
    JSON(3);
    private final int code;


}
