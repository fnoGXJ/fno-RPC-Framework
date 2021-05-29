package com.fno.rpc.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;

public class SingletonFactory {
    private static final ConcurrentHashMap<String, Object> INSTANCE_MAP = new ConcurrentHashMap<>();

    public static <T> T getInstance(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException();
        }
        String key = clazz.toString();
        if (INSTANCE_MAP.containsKey(key)) {
            return clazz.cast(INSTANCE_MAP.get(key));
        } else {
            return clazz.cast(INSTANCE_MAP.computeIfAbsent(key, v -> {
                try {
                    return clazz.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }));
        }
    }
}
