package com.fno.rpc.registry;

import com.fno.rpc.balance.LoadBalance;

import java.net.InetSocketAddress;

public interface ServiceRegistry {
    <T> void registry(String serviceName, InetSocketAddress address);

    InetSocketAddress findServiceAddress(String serviceName, LoadBalance loadBalance);
}
