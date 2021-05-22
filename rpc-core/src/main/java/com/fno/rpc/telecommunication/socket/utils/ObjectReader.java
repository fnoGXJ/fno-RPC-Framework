package com.fno.rpc.telecommunication.socket.utils;

import com.fno.rpc.entity.RpcRequest;
import com.fno.rpc.entity.RpcResponse;
import com.fno.rpc.enumeration.PackageType;
import com.fno.rpc.enumeration.RpcError;
import com.fno.rpc.exception.RpcException;
import com.fno.rpc.serializer.Serializer;
import io.protostuff.Rpc;

import java.io.IOException;
import java.io.InputStream;

public class ObjectReader {
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    public static Object readObject(InputStream inputStream) throws IOException {
        byte[] numbers = new byte[4];
        inputStream.read(numbers);
        int magicNumber = bytesToInt(numbers);
        if (magicNumber != MAGIC_NUMBER) {
            throw new RpcException(RpcError.NOT_RPC_PACKAGE);
        }
        inputStream.read(numbers);
        int packageType = bytesToInt(numbers);
        Class<?> clazz;
        if (packageType == PackageType.REQUEST.getCode()) {
            clazz = RpcRequest.class;
        } else if (packageType == PackageType.RESPONSE.getCode()) {
            clazz = RpcResponse.class;
        } else {
            throw new RpcException(RpcError.WRONG_PACKAGE_TYPE);
        }
        inputStream.read(numbers);
        int serializerCode = bytesToInt(numbers);
        Serializer serializer = Serializer.getSerializerByCode(serializerCode);
        if (serializer == null) {
            throw new RpcException(RpcError.NO_SUCH_SERIALIZER);
        }
        inputStream.read(numbers);
        int length = bytesToInt(numbers);
        byte[] data = new byte[length];
        inputStream.read(data);
        return serializer.deserialize(data, clazz);
    }

    private static int bytesToInt(byte[] numbers) {
        int value;
        value = ((numbers[0] & 0xFF) << 24)
                | ((numbers[1] & 0xFF) << 16)
                | ((numbers[2] & 0xFF) << 8)
                | ((numbers[3] & 0xFF));
        return value;
    }
}
