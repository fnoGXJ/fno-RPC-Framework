package com.fno.rpc.utils;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.fno.rpc.enumeration.RpcError;
import com.fno.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class NacosUtils {
    private static final Logger logger = LoggerFactory.getLogger(NacosUtils.class);
    private static final String REGISTRY_ADDR = "1.117.97.181:8848";
    private static final NamingService namingService;
    private static final Set<String> serviceNames = new HashSet<>();
    private static InetSocketAddress address;

    static {
        namingService = getNacosNamingService();
    }

    public static NamingService getNacosNamingService() {
        try {
            return NamingFactory.createNamingService(REGISTRY_ADDR);
        } catch (NacosException e) {
            logger.error("连接到Nacos时有错误发生: ", e);
            throw new RpcException(RpcError.FAIL_CONNET_NACOS);
        }
    }

    public static void registerService(String serviceName, InetSocketAddress address) {
        serviceNames.add(serviceName);
        NacosUtils.address = address;
    }

    public static List<Instance> getAllInstance(String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }

    public static void clearRegistry() {
        logger.info("开始清除服务...");
        logger.info(serviceNames.toString());
        logger.info(address.toString());
        if (!serviceNames.isEmpty() && address != null) {
            String host = address.getHostName();
            int port = address.getPort();
            Iterator<String> iterator = serviceNames.iterator();
            while (iterator.hasNext()) {
                String service = iterator.next();
                try {
                    namingService.deregisterInstance(service, host, port);
                } catch (NacosException e) {
                    logger.error("注销服务 {} 失败", service, e);
                }
            }
        }
    }
}
