package org.hdl.hpgsc.common.threadpool.limited;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.hdl.hpgsc.Constants;
import org.hdl.hpgsc.common.Configuration;
import org.hdl.hpgsc.common.NamedThreadFactory;
import org.hdl.hpgsc.common.threadpool.ThreadPool;
import org.hdl.hpgsc.common.threadpool.support.AbortPolicyWithReport;

/**
 * 此线程池一直增长，直到上限，增长后不收缩。
 * 
 * @author <a href="mailto:gang.lvg@alibaba-inc.com">kimi</a>
 */
public class LimitedThreadPool implements ThreadPool {

    public Executor getExecutor(Configuration conf) {
        String name = conf.get(Constants.THREAD_NAME_KEY, Constants.DEFAULT_THREAD_NAME);
        int cores = conf.getInt(Constants.CORE_THREADS_KEY, Constants.DEFAULT_CORE_THREADS);
        int threads = conf.getInt(Constants.THREADS_KEY, Constants.DEFAULT_THREADS);
        int queues = conf.getInt(Constants.QUEUES_KEY, Constants.DEFAULT_QUEUES);
        return new ThreadPoolExecutor(cores, threads, Long.MAX_VALUE, TimeUnit.MILLISECONDS, 
        		queues == 0 ? new SynchronousQueue<Runnable>() : 
        			(queues < 0 ? new LinkedBlockingQueue<Runnable>() 
        					: new LinkedBlockingQueue<Runnable>(queues)),
        		new NamedThreadFactory(name, true), new AbortPolicyWithReport(name));
    }

}
