package com.fno.rpc.telecommunication;

import com.fno.rpc.annotation.Service;
import com.fno.rpc.annotation.ServiceScan;
import com.fno.rpc.enumeration.RpcError;
import com.fno.rpc.exception.RpcException;
import com.fno.rpc.factory.SingletonFactory;
import com.fno.rpc.provider.DefaultServiceProvider;
import com.fno.rpc.provider.ServiceProvider;
import com.fno.rpc.registry.NacosServiceRegistry;
import com.fno.rpc.registry.ServiceRegistry;
import com.fno.rpc.serializer.Serializer;
import com.fno.rpc.utils.NacosUtils;
import com.fno.rpc.utils.ReflectUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Set;

@Slf4j
public abstract class AbstractRpcServer implements RpcServer {
    protected int port;
    protected ServiceRegistry serviceRegistry = SingletonFactory.getInstance(NacosServiceRegistry.class);
    protected ServiceProvider serviceProvider = SingletonFactory.getInstance(DefaultServiceProvider.class);
    protected Serializer serializer;

    public void scanServices() {
        String mainClassName = ReflectUtils.getMainClass();
        Class<?> mainClass;
        try {
            mainClass = Class.forName(mainClassName);
            if (!mainClass.isAnnotationPresent(ServiceScan.class)) {
                log.error("主类中没有@ServiceScan注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            log.error("获取注解出错...");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        String rootPackage = mainClass.getAnnotation(ServiceScan.class).value();
        if ("".equals(rootPackage)) {
            rootPackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }
        Set<Class<?>> classes = ReflectUtils.getClasses(rootPackage);
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(Service.class)) {
                String serviceName = clazz.getAnnotation(Service.class).name();
                Object obj;
                try {
                    obj = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                    continue;
                }
                if ("".equals(serviceName)) {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class inter : interfaces) {
                        publishService(obj, inter.getCanonicalName());
                    }
                } else {
                    publishService(obj, serviceName);
                }
            }
        }
    }

    @Override
    public <T> void publishService(T service, String serviceName) {
        InetSocketAddress address = new InetSocketAddress("localhost", port);
        NacosUtils.registerService(serviceName, address);
        serviceProvider.addService(service);
        serviceRegistry.registry(serviceName, address);
    }
}
