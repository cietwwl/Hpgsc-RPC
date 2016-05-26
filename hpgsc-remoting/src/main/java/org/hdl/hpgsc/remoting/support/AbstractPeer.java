package org.hdl.hpgsc.remoting.support;

import org.hdl.hpgsc.common.Configuration;
import org.hdl.hpgsc.common.utils.Preconditions;
import org.hdl.hpgsc.remoting.Channel;
import org.hdl.hpgsc.remoting.ChannelHandler;
import org.hdl.hpgsc.remoting.ChannelHandlerDelegate;
import org.hdl.hpgsc.remoting.Endpoint;
import org.hdl.hpgsc.remoting.RemotingException;


/**
 * Abstract peer
 * @author qiuhd
 * @since  2014年9月3日
 */
public abstract class AbstractPeer implements Endpoint, ChannelHandler {

	protected final ChannelHandler handler;
	private volatile boolean closed = false;
	protected Configuration conf ;
	
	public AbstractPeer(Configuration conf,ChannelHandler handler) {
		Preconditions.checkArgument(conf != null, "conf == null");
		Preconditions.checkArgument(handler != null,"handler == null");
		this.conf = conf;
		this.handler = handler;
	}
	
	@Override
	public Configuration getConf() {
		return this.conf ;
	}

	@Override
	public void send(Object message) throws RemotingException {
		send(message, false);
	}

	@Override
	public ChannelHandler getChannelHandler() {
		if (handler instanceof ChannelHandlerDelegate) {
			return ((ChannelHandlerDelegate)handler).getChannelHandler();
		}
		return handler;
	}

	@Override
	public void close() {
		this.closed = true;
	}

	@Override
    public void close(int timeout) {
        close();
    }

	@Override
	public boolean isClosed() {
		return closed;
	}
	
	@Override
	public void connected(Channel channel) throws RemotingException {
		if (closed)
			return;
		handler.connected(channel);
	}

	@Override
	public void disconnected(Channel channel) throws RemotingException {
		handler.disconnected(channel);
	}

	@Override
	public void sent(Channel channel, Object message) throws RemotingException {
		if (closed)
			return;
		handler.sent(channel, message);
	}

	@Override
	public void received(Channel channel, Object message) throws RemotingException {
		if (closed)
			return;
		handler.received(channel, message);
	}

	@Override
	public void caught(Channel channel, Throwable t) throws RemotingException {
		handler.caught(channel, t);
	}
}

