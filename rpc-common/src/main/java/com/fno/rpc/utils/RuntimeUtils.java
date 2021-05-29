package com.fno.rpc.utils;

public class RuntimeUtils {
    public static Integer getCPUs() {
        return Runtime.getRuntime().availableProcessors();
    }
}
