package com.fno.rpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fno.rpc.entity.RpcRequest;
import com.fno.rpc.entity.RpcResponse;
import com.fno.rpc.enumeration.SerializerCode;
import com.sun.xml.internal.ws.encoding.soap.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoSerializer implements Serializer {
    private final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    public byte[] serialize(Object obj) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream);
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            logger.error("序列化时有错误发生:", e);
            throw new SerializationException("序列化时有错误发生");
        }
    }

    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            Input input = new Input(byteArrayInputStream);
            Kryo kryo = kryoThreadLocal.get();
            Object ret = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return ret;
        } catch (Exception e) {
            logger.error("反序列化时有错误发生:", e);
            throw new SerializationException("反序列化时有错误发生");
        }
    }

    public int getCode() {
        return SerializerCode.KRYO.getCode();
    }
}
