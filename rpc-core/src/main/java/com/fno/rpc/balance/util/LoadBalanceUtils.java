package com.fno.rpc.balance.util;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.fno.rpc.annotation.ClientLoadBalance;
import com.fno.rpc.balance.ConsistentHashLoadBalance;
import com.fno.rpc.balance.LoadBalance;
import com.fno.rpc.balance.RandomLoadBalance;
import com.fno.rpc.balance.RoundRobinLoadBalance;
import com.fno.rpc.balance.LeastActiveLoadBalance;
import com.fno.rpc.enumeration.LoadBalanceType;
import com.fno.rpc.enumeration.RpcError;
import com.fno.rpc.exception.RpcException;
import com.fno.rpc.telecommunication.RpcClient;
import com.fno.rpc.utils.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LoadBalanceUtils {
    private static Logger logger = LoggerFactory.getLogger(LoadBalanceUtils.class);

    private static LoadBalance getLoadBalance() {
        String mainClassName = ReflectUtils.getMainClass();
        Class<?> mainClass;
        LoadBalance loadBalance;
        try {
            mainClass = Class.forName(mainClassName);
            if (!mainClass.isAnnotationPresent(ClientLoadBalance.class)) {
                loadBalance = new RandomLoadBalance();
            } else {
                String loadBalanceType = mainClass.getAnnotation(ClientLoadBalance.class).name();
                Integer code = LoadBalanceType.getLoadBalanceType(loadBalanceType).getCode();
                loadBalance = getLoadBalanceByCode(code);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        return loadBalance;
    }

    public static Instance getInstance(List<Instance> allInstances, RpcClient rpcClient) {
        LoadBalance loadBalance = getLoadBalance();
        logger.info("loadBalance：{}", loadBalance);
        return loadBalance.select(allInstances, rpcClient);
    }

    private static LoadBalance getLoadBalanceByCode(Integer loadBalanceCode) {
        switch (loadBalanceCode) {
            case 1:
                return new RandomLoadBalance();
            case 2:
                return new RoundRobinLoadBalance();
            case 3:
                return new ConsistentHashLoadBalance();
            case 4:
                return new LeastActiveLoadBalance();
            default:
                return new RandomLoadBalance();
        }
    }
}
