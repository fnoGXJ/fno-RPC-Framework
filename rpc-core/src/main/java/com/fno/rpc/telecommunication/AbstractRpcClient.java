package com.fno.rpc.telecommunication;

import com.fno.config.ClientConfigUtils;
import com.fno.config.ClientConfiguration;
import com.fno.rpc.registry.NacosServiceRegistry;
import com.fno.rpc.registry.ServiceRegistry;

public abstract class AbstractRpcClient implements RpcClient {
    protected static ServiceRegistry serviceRegistry;
    protected static ClientConfiguration configuration;

    public void load() {
        configuration = ClientConfigUtils.load();
        serviceRegistry = new NacosServiceRegistry(this);
    }
}
