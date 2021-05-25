package com.fno.rpc.balance;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.fno.rpc.telecommunication.RpcClient;

import java.util.List;

public class LeastActiveLoadBalance extends AbstractLoadBalance {
    @Override
    protected Instance doSelect(List<Instance> allInstances, RpcClient rpcClient) {
        return null;
    }
}
