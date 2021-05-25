package com.fno.rpc.balance;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.fno.rpc.telecommunication.RpcClient;

import java.util.List;

public interface LoadBalance {
    Instance select(List<Instance> allInstances, RpcClient rpcClient);
}
