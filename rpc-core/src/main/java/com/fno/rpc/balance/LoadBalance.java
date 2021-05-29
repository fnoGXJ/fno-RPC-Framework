package com.fno.rpc.balance;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.fno.rpc.telecommunication.RpcClient;

import java.util.List;

public interface LoadBalance {
    Instance select(List<Instance> allInstances, RpcClient rpcClient);

    static LoadBalance getLoadBalanceByCode(Integer loadBalanceCode) {
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
