package com.fno.rpc.serializer;

public interface Serializer {

    byte[] serialize(Object obj);

    Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();

    static Serializer getSerializerByCode(int code) {
        switch (code) {
            case 1:
                return new KryoSerializer();
            case 2:
                return new ProtobufSerializer();
            case 3:
                return new JsonSerializer();
            default:
                return new ProtobufSerializer();
        }
    }
}
