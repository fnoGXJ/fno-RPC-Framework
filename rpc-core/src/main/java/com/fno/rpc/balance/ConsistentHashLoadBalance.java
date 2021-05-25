package com.fno.rpc.balance;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.fno.rpc.telecommunication.RpcClient;
import io.protostuff.Rpc;

import java.util.List;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class ConsistentHashLoadBalance extends AbstractLoadBalance {
    private static final ConcurrentSkipListMap<Integer, Instance> skipList = new ConcurrentSkipListMap();

    @Override
    protected Instance doSelect(List<Instance> allInstances, RpcClient rpcClient) {
        for (Instance instance : allInstances) {
            int instanceHash = hash(instance.getIp());
            if (skipList.containsKey(instanceHash)) {
                skipList.put(instanceHash, instance);
            }
        }
        int clientHash = hash(rpcClient);
        ConcurrentNavigableMap<Integer, Instance> tailMap = skipList.tailMap(clientHash);
        if (tailMap.firstEntry() == null) {
            tailMap = skipList.headMap(clientHash);
        }
        return tailMap.firstEntry().getValue();
    }

    private int hash(Object obj) {
        return obj.hashCode();
    }
}
