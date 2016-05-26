package org.hdl.hpgsc.remoting.support;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.concurrent.Executor;

import org.hdl.hpgsc.Constants;
import org.hdl.hpgsc.common.Configuration;
import org.hdl.hpgsc.common.utils.ExecutorUtil;
import org.hdl.hpgsc.remoting.Channel;
import org.hdl.hpgsc.remoting.ChannelHandler;
import org.hdl.hpgsc.remoting.Codec;
import org.hdl.hpgsc.remoting.RemotingException;
import org.hdl.hpgsc.remoting.Server;
import org.hdl.hpgsc.remoting.dispatcher.WrappedChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Abstract Server
 * @author qiuhd
 * @since  2014-7-24
 * @version V1.0.0
 */
public abstract class AbstractServer extends AbstractEndpoint implements Server {

	private final static Logger logger = LoggerFactory.getLogger(AbstractServer.class);
	private InetSocketAddress bindAddress ;
	private int maxClients; 
	private Executor executor;
	protected static final String SERVER_THREAD_POOL_NAME  = "ServerHandler";
	
	public AbstractServer(Configuration conf, ChannelHandler handler,Codec codec) throws RemotingException{
		super(conf, handler,codec);
		
		this.maxClients = conf.getInt(Constants.MAX_CLIENTS, Constants.DEFAULT_MAX_CLIENTS);
		
		String host = conf.get(Constants.BIND_HOST, Constants.DEFAULT_BIND_HOST);
		int port = conf.getInt(Constants.BIND_PORT, Constants.DEFAULT_BIND_PORT);
		this.bindAddress = new InetSocketAddress(host, port);
		
		try {
			doOpen();
			logger.info("Start " + getClass().getSimpleName() + " bind " + getBindAddress());
		}catch (Throwable t) {
			 throw new RemotingException(null, null, "Failed to bind " + getClass().getSimpleName() 
                     + " on " + getBindAddress() + ", cause: " + t.getMessage(), t);
		}
		
		if (handler instanceof WrappedChannelHandler){
            executor = ((WrappedChannelHandler)handler).getExecutor();
        }
	}

	public abstract void doOpen() throws Throwable ;
	
	public abstract void doClose() throws Throwable ;

	@Override
	public void close() {
		logger.info("Close " + getClass().getSimpleName() + " bind "+ getBindAddress() + ", export " + getLocalAddress());
		ExecutorUtil.shutdownNow(executor, 100);
		try {
			super.close();
		} catch (Throwable e) {
			logger.warn(e.getMessage(), e);
		}
		try {
			doClose();
		} catch (Throwable e) {
			logger.warn(e.getMessage(), e);
		}
	}

	@Override
    public void close(int timeout) {
        ExecutorUtil.gracefulShutdown(executor ,timeout);
        close();
    }
	
	public InetSocketAddress getBindAddress() {
		return this.bindAddress;
	}

    @Override
	public InetSocketAddress getLocalAddress() {
    	return this.bindAddress;
	}

	@Override
	public void send(Object message) throws RemotingException {
		Collection<Channel> channels = getChannels();
		for (Channel channel : channels) {
			if (channel.isConnected()) {
				channel.send(message);
			}
		}
	}
	
	public void send(Object message, boolean sent) throws RemotingException {
		Collection<Channel> channels = getChannels();
		for (Channel channel : channels) {
			if (channel.isConnected()) {
				channel.send(message, sent);
			}
		}
	}

	public int getMaxClients() {
		return this.maxClients;
	}

	@Override
	public void connected(Channel ch) throws RemotingException {
		Collection<Channel> channels = getChannels();
		if (maxClients > 0 && channels.size() > maxClients) {
			logger.error("Close channel " + ch + ", cause: The server "+ ch.getLocalAddress()+ " connections greater than max config " + maxClients);
			ch.close();
			return;
		}
		super.connected(ch);
	}
}

