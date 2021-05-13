package com.fno.rpc.serializer;

public interface Serializer {

    byte[] serialize(Object obj);

    Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();

    static Serializer getSerializerByCode(int code) {
        switch (code) {
            case 2:
                return new JsonSerializer();
            case 1:
                return new KryoSerializer();
            default:
                return new KryoSerializer();
        }
    }
}
