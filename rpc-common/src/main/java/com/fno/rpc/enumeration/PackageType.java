package com.fno.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PackageType {
    REQUEST(1),
    RESPONSE(2);
    private int code;
}
