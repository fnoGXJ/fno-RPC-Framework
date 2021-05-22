package com.fno.rpc.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

public class ThreadPoolUtils {
    private static final int CORE_POOL_SIZE = 10;
    private static final int MAXIMUM_POOL_SIZE = 100;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;

    public ThreadPoolUtils() {

    }

    public static ExecutorService createThreadPool(String threadNamePrefix) {
        return createThreadPool(threadNamePrefix, false);
    }

    public static ExecutorService createThreadPool(String threadNamePrefix, Boolean isDaemon) {
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, isDaemon);
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue(BLOCKING_QUEUE_CAPACITY);
        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue, threadFactory);
    }

    public static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean isDaemon) {
        if (threadNamePrefix == null) {
            if (isDaemon == null) {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            } else {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").setDaemon(isDaemon).build();
            }
        }
        return Executors.defaultThreadFactory();
    }
}
