package com.fno.rpc.codec;

import com.fno.rpc.entity.HeartbeatMsg;
import com.fno.rpc.entity.RpcRequest;
import com.fno.rpc.entity.RpcResponse;
import com.fno.rpc.enumeration.PackageType;
import com.fno.rpc.enumeration.RpcError;
import com.fno.rpc.exception.RpcException;
import com.fno.rpc.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CommonDecoder extends ReplayingDecoder {
    private static final int MAGIC_NUMBER = 0xCAFEBABE;
    private final Logger logger = LoggerFactory.getLogger(CommonEncoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNumber = in.readInt();
        if (magicNumber != MAGIC_NUMBER) {
            logger.error("错误的包，接收到的包不是RPC协议包");
            throw new RpcException(RpcError.NOT_RPC_PACKAGE);
        }
        int packageCode = in.readInt();
        Class<?> packageType;
        if (packageCode == PackageType.REQUEST.getCode()) {
            packageType = RpcRequest.class;
        } else if (packageCode == PackageType.RESPONSE.getCode()) {
            packageType = RpcResponse.class;
        } else if (packageCode == PackageType.HEARTBEAT.getCode()) {
            packageType = HeartbeatMsg.class;
        } else {
            logger.error("无法识别的类型编号");
            throw new RpcException(RpcError.WRONG_PACKAGE_TYPE);
        }
        int serializerCode = in.readInt();
        Serializer serializer = Serializer.getSerializerByCode(serializerCode);
        if (serializer == null) {
            logger.error("无法识别的序列化器");
            throw new RpcException(RpcError.NO_SUCH_SERIALIZER);
        }
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        Object ret = serializer.deserialize(bytes, packageType);
        out.add(ret);
    }
}
