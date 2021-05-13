package com.fno.rpc.registry;

import java.net.InetSocketAddress;

public interface ServiceRegistry {
    void registry(String serviceName, InetSocketAddress address);

    InetSocketAddress findServiceAddress(String serviceName);
}
