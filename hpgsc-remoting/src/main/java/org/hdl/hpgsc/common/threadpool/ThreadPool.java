package org.hdl.hpgsc.common.threadpool;

import java.util.concurrent.Executor;

import org.hdl.hpgsc.common.Configuration;

/**
 * ThreadPool
 * @author qiuhd
 *
 */
public interface ThreadPool {
    
    /**
     * 线程池
     * 
     * @param conf 线程参数
     * @return 线程池
     */
    Executor getExecutor(Configuration conf);
}