package com.fno.rpc.balance;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.fno.rpc.telecommunication.RpcClient;
import com.google.common.util.concurrent.AtomicDouble;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalance extends AbstractLoadBalance {
    private static final ConcurrentMap<Instance, AtomicDouble> map = new ConcurrentHashMap<>();

    @Override
    protected Instance doSelect(List<Instance> allInstances, RpcClient rpcClient) {
        Instance bestInstance = null;
        double totalWeight = 0;
        for (Instance instance : allInstances) {
            AtomicDouble aDouble = map.get(instance);
            if (aDouble == null) {
                aDouble = new AtomicDouble(0.0);
                map.putIfAbsent(instance, aDouble);
            }
            double weight = instance.getWeight();
            aDouble.addAndGet(weight);
            totalWeight += weight;
            if (bestInstance == null || map.get(bestInstance).get() < aDouble.get()) {
                bestInstance = instance;
            }
        }
        AtomicDouble aDouble = map.get(bestInstance);
        aDouble.set(totalWeight - aDouble.get());
        return bestInstance;
    }
}
