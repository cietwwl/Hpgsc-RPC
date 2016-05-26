package org.hdl.hpgsc.remoting.dispatcher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.hdl.hpgsc.Constants;
import org.hdl.hpgsc.common.Configuration;
import org.hdl.hpgsc.common.NamedThreadFactory;
import org.hdl.hpgsc.common.threadpool.ThreadPool;
import org.hdl.hpgsc.common.threadpool.cached.CachedThreadPool;
import org.hdl.hpgsc.common.threadpool.fixed.FixedThreadPool;
import org.hdl.hpgsc.common.threadpool.limited.LimitedThreadPool;
import org.hdl.hpgsc.remoting.Channel;
import org.hdl.hpgsc.remoting.ChannelHandler;
import org.hdl.hpgsc.remoting.ChannelHandlerDelegate;
import org.hdl.hpgsc.remoting.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Wrapped Channel Handler
 * @author qiuhd
 * @since  2014年9月3日
 */
public class WrappedChannelHandler implements ChannelHandlerDelegate {
    
    protected static final Logger logger = LoggerFactory.getLogger(WrappedChannelHandler.class);

    protected static final ExecutorService SHARED_EXECUTOR = Executors.newCachedThreadPool(new NamedThreadFactory("SharedHandler", true));
    
    protected final ExecutorService executor;
    
    protected final ChannelHandler handler;

    protected final Configuration conf;
    
    public WrappedChannelHandler(ChannelHandler handler, Configuration conf) {
        this.handler = handler;
        this.conf = conf;
        String threadPool = this.conf.get(Constants.THREADPOOL_KEY,Constants.DEFAULT_THREADPOOL);
        executor = (ExecutorService) getThreadPool(threadPool).getExecutor(conf);
    }
    
    private ThreadPool getThreadPool(String threadPool) {
    	if (threadPool.equals(Constants.CACHED_THREADPOOL_KEY)) {
    		return new CachedThreadPool() ;
    	}else if (threadPool.equals(Constants.FIXED_THREADPOOL_KEY)){
    		return new FixedThreadPool();
    	}else if (threadPool.endsWith(Constants.LIMITED_THREADPOOL_KEY)){
    		return new LimitedThreadPool();
    	}
    	return null;
    }
    
    public void close() {
        try {
            if (executor instanceof ExecutorService) {
                ((ExecutorService)executor).shutdown();
            }
        } catch (Throwable t) {
            logger.warn("fail to destroy thread pool of server: " + t.getMessage(), t);
        }
    }

    public void connected(Channel channel) throws RemotingException {
        handler.connected(channel);
    }

    public void disconnected(Channel channel) throws RemotingException {
        handler.disconnected(channel);
    }

    public void sent(Channel channel, Object message) throws RemotingException {
        handler.sent(channel, message);
    }

    public void received(Channel channel, Object message) throws RemotingException {
        handler.received(channel, message);
    }

    public void caught(Channel channel, Throwable exception) throws RemotingException {
        handler.caught(channel, exception);
    }
    
    public ExecutorService getExecutor() {
        return executor;
    }
    
    public ChannelHandler getChannelHandler() {
        if (handler instanceof ChannelHandlerDelegate) {
            return ((ChannelHandlerDelegate) handler).getChannelHandler();
        } else {
            return handler;
        }
    }
    
    public Configuration getAppConf() {
        return conf;
    }
}