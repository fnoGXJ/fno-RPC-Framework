package com.fno.config;

import com.fno.rpc.annotation.ReferenceConfig;
import com.fno.rpc.balance.LoadBalance;
import com.fno.rpc.balance.RandomLoadBalance;
import com.fno.rpc.enumeration.LoadBalanceType;
import com.fno.rpc.enumeration.SerializerCode;
import com.fno.rpc.serializer.KryoSerializer;
import com.fno.rpc.serializer.Serializer;
import com.fno.rpc.utils.ReflectUtils;

public class ClientConfigUtils {
    private static Class<?> mainClass;

    static {
        String mainClassName = ReflectUtils.
                getMainClass();
        try {
            mainClass = Class.forName(mainClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static ClientConfiguration load() {
        ClientConfiguration configuration = new ClientConfiguration();
        if (mainClass.isAnnotationPresent(ReferenceConfig.class)) {
            LoadBalance loadBalance = configLoadBalance();
            Serializer serializer = configSerializer();
            configuration.setLoadBalance(loadBalance);
            configuration.setSerializer(serializer);
        } else {
            configuration.setLoadBalance(new RandomLoadBalance());
            configuration.setSerializer(new KryoSerializer());
        }
        return configuration;
    }

    private static LoadBalance configLoadBalance() {
        String loadBalanceType = mainClass.getAnnotation(ReferenceConfig.class).loadBalance();
        Integer code = LoadBalanceType.getLoadBalanceType(loadBalanceType).getCode();
        return LoadBalance.getLoadBalanceByCode(code);
    }

    private static Serializer configSerializer() {
        String serializerCode = mainClass.getAnnotation(ReferenceConfig.class).serializer();
        return Serializer.getSerializerByCode(SerializerCode.valueOf(serializerCode).getCode());
    }
}
