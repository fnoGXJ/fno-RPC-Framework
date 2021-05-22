package com.fno.rpc.hook;

import com.fno.rpc.utils.NacosUtils;
import com.fno.rpc.utils.ThreadPoolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public class ShutdownHook {
    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);
    private final ExecutorService threadPool = ThreadPoolUtils.createThreadPool("shutdown-hook");
    private static final ShutdownHook shutdownHook = new ShutdownHook();
    public static ShutdownHook getShutdownHook() {
        return shutdownHook;
    }
    public void addClearAllHook() {
        logger.info("关闭后将自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtils.clearRegistry();
            threadPool.shutdown();
        }));
    }
}