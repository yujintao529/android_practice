package com.demon.yu.utils;

import com.example.mypractice.Logger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolUtils {

    private static Executor ioExecutor;


    public static void init() {
        if (ioExecutor == null) {
            ioExecutor = new ThreadPoolExecutor(2, 5, 30, TimeUnit.SECONDS
                    , new ArrayBlockingQueue(20, false), new ThreadFactory() {
                private AtomicInteger atomicInteger = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "ioThread-" + atomicInteger.getAndAdd(1));
                }
            }, new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    Logger.debug("ThreadPoolUtils", "rejectedExecution " + r);
                }
            });
        }
    }

    public static Executor getIoExecutor() {
        return ioExecutor;
    }
}
