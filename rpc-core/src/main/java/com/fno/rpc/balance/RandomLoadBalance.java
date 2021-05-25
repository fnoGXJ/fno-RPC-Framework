package com.fno.rpc.balance;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.fno.rpc.telecommunication.RpcClient;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomLoadBalance extends AbstractLoadBalance {
    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    @Override
    protected Instance doSelect(List<Instance> allInstances, RpcClient rpcClient) {
        int length = allInstances.size();
        int totalWeight = 0;
        for (Instance instance : allInstances) {
            totalWeight += instance.getWeight();
        }
        int randomWeight = random.nextInt(totalWeight);
        for (Instance instance : allInstances) {
            randomWeight -= instance.getWeight();
            if (randomWeight < 0) {
                return instance;
            }
        }
        return allInstances.get(random.nextInt());
    }
}
