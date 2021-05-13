package com.fno.rpc.codec;

import com.fno.rpc.entity.RpcRequest;
import com.fno.rpc.entity.RpcResponse;
import com.fno.rpc.enumeration.PackageType;
import com.fno.rpc.enumeration.RpcError;
import com.fno.rpc.exception.RpcException;
import com.fno.rpc.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CommonEncoder extends MessageToByteEncoder {
    private static final int MAGIC_NUMBER = 0xCAFEBABE;
    private final Logger logger = LoggerFactory.getLogger(CommonEncoder.class);
    private final Serializer serializer;

    public CommonEncoder(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        out.writeInt(MAGIC_NUMBER);
        if (msg instanceof RpcRequest) {
            out.writeInt(PackageType.REQUEST.getCode());
        } else{
            out.writeInt(PackageType.RESPONSE.getCode());
        }
        out.writeInt(serializer.getCode());
        byte[] bytes = serializer.serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
