package com.fno.rpc.telecommunication.socket.utils;

import com.fno.rpc.entity.RpcRequest;
import com.fno.rpc.entity.RpcResponse;
import com.fno.rpc.enumeration.PackageType;
import com.fno.rpc.enumeration.ResponseCode;
import com.fno.rpc.enumeration.RpcError;
import com.fno.rpc.enumeration.SerializerCode;
import com.fno.rpc.exception.RpcException;
import com.fno.rpc.serializer.Serializer;
import io.protostuff.Rpc;

import java.io.IOException;
import java.io.OutputStream;

public class ObjectWriter {
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    public static void writeObject(OutputStream outputStream, Object obj, Serializer serializer) throws IOException {
        outputStream.write(intToBytes(MAGIC_NUMBER));
        if (obj instanceof RpcRequest) {
            outputStream.write(intToBytes(PackageType.REQUEST.getCode()));
        } else if (obj instanceof RpcResponse) {
            outputStream.write(intToBytes(PackageType.RESPONSE.getCode()));
        } else {
            throw new RpcException(RpcError.PACKAGE_TYPE_NOT_DEFINED);
        }
        outputStream.write(intToBytes(serializer.getCode()));
        byte[] bytes = serializer.serialize(obj);
        outputStream.write(intToBytes(bytes.length));
        outputStream.write(bytes);
        outputStream.flush();
    }

    private static byte[] intToBytes(int value) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((value >> 24) & 0xFF);
        bytes[1] = (byte) ((value >> 16) & 0xFF);
        bytes[2] = (byte) ((value >> 8) & 0xFF);
        bytes[3] = (byte) (value & 0xFF);
        return bytes;
    }
}
