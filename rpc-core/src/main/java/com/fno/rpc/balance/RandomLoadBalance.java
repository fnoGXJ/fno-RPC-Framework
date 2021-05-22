package com.fno.rpc.balance;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance implements LoadBalance {
    @Override
    public Instance select(List<Instance> allInstances) {
        return allInstances.get(new Random().nextInt(allInstances.size()));
    }
}
