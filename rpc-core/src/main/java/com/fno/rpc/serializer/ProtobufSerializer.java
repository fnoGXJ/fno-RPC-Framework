package com.fno.rpc.serializer;

import com.fno.rpc.enumeration.SerializerCode;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ProtobufSerializer implements Serializer {
    private final LinkedBuffer linkedBuffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    private final Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();

    public byte[] serialize(Object obj) {
        Class clazz = obj.getClass();
        Schema schema = getSchema(clazz);
        byte[] bytes;
        try {
            bytes = ProtobufIOUtil.toByteArray(obj, schema, linkedBuffer);
        } finally {
            linkedBuffer.clear();
        }
        return bytes;
    }

    public Object deserialize(byte[] bytes, Class<?> clazz) {
        Schema schema = getSchema(clazz);
        Object obj = schema.newMessage();
        ProtobufIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }

    public Schema getSchema(Class<?> clazz) {
        Schema schema = schemaCache.get(clazz);
        if (Objects.isNull(schema)) {
            schema = RuntimeSchema.getSchema(clazz);
            if (Objects.nonNull(schema)) {
                schemaCache.put(clazz, schema);
            }
        }
        return schema;
    }

    public int getCode() {
        return SerializerCode.PROTOBUF.getCode();
    }

}
