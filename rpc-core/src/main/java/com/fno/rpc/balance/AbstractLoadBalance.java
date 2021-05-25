package com.fno.rpc.balance;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.fno.rpc.telecommunication.RpcClient;

import java.util.List;

public abstract class AbstractLoadBalance implements LoadBalance {
    @Override
    public Instance select(List<Instance> allInstances, RpcClient rpcClient) {
        if (allInstances == null || allInstances.size() == 0) {
            return null;
        }
        if (allInstances.size() == 1) {
            return allInstances.get(0);
        }
        return doSelect(allInstances, rpcClient);
    }

    protected abstract Instance doSelect(List<Instance> allInstances, RpcClient rpcClient);
}
