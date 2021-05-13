package com.fno.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.fno.rpc.enumeration.RpcError;
import com.fno.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosServiceRegistry implements ServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);
    private static final String REGISTRY_ADDR = "1.117.97.181:8848";
    private static final NamingService namingService;

    static {
        try {
            namingService = NamingFactory.createNamingService(REGISTRY_ADDR);
        } catch (NacosException e) {
            logger.error("连接nacos时出错...", e);
            throw new RpcException(RpcError.FAIL_CONNET_NACOS);
        }
    }

    @Override
    public void registry(String serviceName, InetSocketAddress address) {
        try {
            namingService.registerInstance(serviceName, address.getHostName(), address.getPort());
        } catch (NacosException e) {
            logger.error("向nacos服务注册失败...", e);
            throw new RpcException(RpcError.FAIL_REGISTRY_SERVICE);
        }
    }

    @Override
    public InetSocketAddress findServiceAddress(String serviceName) {
        try {
            List<Instance> allInstances = namingService.getAllInstances(serviceName);
            Instance instance = allInstances.get(0);
            return new InetSocketAddress(instance.getIp(),instance.getPort());
        } catch (NacosException e) {
            logger.error("获取服务器实例错误...", e);
            throw new RpcException(RpcError.FAIL_GET_SERVICE);
        }
    }
}
