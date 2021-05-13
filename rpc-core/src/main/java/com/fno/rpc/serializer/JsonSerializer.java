package com.fno.rpc.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fno.rpc.entity.RpcRequest;
import com.fno.rpc.entity.RpcResponse;
import com.fno.rpc.enumeration.SerializerCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JsonSerializer implements Serializer {
    private final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(Object obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logger.error("序列化对象失败: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            Object ret = objectMapper.readValue(bytes, clazz);
            if(ret instanceof RpcRequest){
                return handleRequest(ret);
            }else if(ret instanceof RpcResponse){
                return (RpcResponse) ret;
            }else{
                logger.error("无法识别的对象类型，JSON解析错误...");
                return null;
            }
        } catch (IOException e) {
            logger.error("解析Json字符串时出错: {}", e.getMessage());
            return null;
        }
    }
    public RpcRequest handleRequest(Object obj) throws IOException {
        RpcRequest rpcRequest = (RpcRequest) obj;
        for(int i = 0; i < rpcRequest.getParamTypes().length; i ++) {
            Class<?> clazz = rpcRequest.getParamTypes()[i];
            if(!clazz.isAssignableFrom(rpcRequest.getParameters()[i].getClass())) {
                byte[] bytes = objectMapper.writeValueAsBytes(rpcRequest.getParameters()[i]);
                rpcRequest.getParameters()[i] = objectMapper.readValue(bytes, clazz);
            }
        }
        return rpcRequest;
    }
    @Override
    public int getCode() {
        return SerializerCode.JSON.getCode();
    }
}
