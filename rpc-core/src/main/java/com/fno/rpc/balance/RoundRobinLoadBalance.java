package com.fno.rpc.balance;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalance implements LoadBalance {
    private volatile int index;

    @Override
    public Instance select(List<Instance> allInstances) {
        Instance instance = allInstances.get(index);
        int size = allInstances.size();
        if(index >= size-1){
            synchronized (this){
                if(index >= size){
                    index = index % size;
                }
            }
        }
        return instance;
    }
}
