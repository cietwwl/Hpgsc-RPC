package org.hdl.hpgsc.remoting.support;

import org.hdl.hpgsc.common.utils.Preconditions;
import org.hdl.hpgsc.remoting.Channel;
import org.hdl.hpgsc.remoting.ChannelHandler;
import org.hdl.hpgsc.remoting.ChannelHandlerDelegate;
import org.hdl.hpgsc.remoting.RemotingException;

/**
 * Abstract channel handler delegate
 * @author qiuhd
 * @since  2014年9月3日
 */
public abstract class AbstractChannelHandlerDelegate implements ChannelHandlerDelegate {

	protected final ChannelHandler handler;
	
	public AbstractChannelHandlerDelegate(ChannelHandler channelHandler) {
		Preconditions.checkArgument(channelHandler != null,"channelHanlder == null");
		this.handler = channelHandler;
	}
	
	@Override
	public ChannelHandler getChannelHandler() {
		return this.handler;
	}

	@Override
	public void connected(Channel channel) throws RemotingException {
		handler.connected(channel);
	}

	@Override
	public void disconnected(Channel channel) throws RemotingException {
		handler.disconnected(channel);
	}

	@Override
	public void sent(Channel channel, Object message) throws RemotingException {
		handler.sent(channel, message);
	}

	@Override
	public void received(Channel channel, Object message)throws RemotingException {
		handler.received(channel, message);
	}

	@Override
	public void caught(Channel channel, Throwable t) throws RemotingException {
		handler.caught(channel, t);
	}
}

