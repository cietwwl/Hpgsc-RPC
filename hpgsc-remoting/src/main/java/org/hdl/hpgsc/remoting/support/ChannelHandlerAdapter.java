package org.hdl.hpgsc.remoting.support;

import org.hdl.hpgsc.remoting.Channel;
import org.hdl.hpgsc.remoting.ChannelHandler;
import org.hdl.hpgsc.remoting.RemotingException;

/**
 * Channel handler adapter
 * @author qiuhd
 */
public class ChannelHandlerAdapter implements ChannelHandler {

	@Override
	public void caught(Channel channel,Throwable cause ) throws RemotingException {
	}

	@Override
	public void connected(Channel channel) throws RemotingException {
		//do nothing
	}

	@Override
	public void disconnected(Channel channel) throws RemotingException {
		//do nothing
	}

	@Override
	public void received(Channel channel,Object message) throws RemotingException {
		//do nothing
	}

	@Override
	public void sent(Channel channel,Object message) throws RemotingException {
		//do nothing
	}
}
