package org.hdl.hpgsc.remoting.support;

import org.hdl.hpgsc.common.Configuration;
import org.hdl.hpgsc.remoting.Channel;
import org.hdl.hpgsc.remoting.ChannelHandler;
import org.hdl.hpgsc.remoting.RemotingException;

/**
 * Abstract channel
 * @author qiuhd
 * @since  2014-8-1
 * @version V1.0.0
 */
public abstract class AbstractChannel extends AbstractPeer implements Channel{

	
	public AbstractChannel(Configuration conf, ChannelHandler handler) {
		super(conf, handler);
	}

	public void send(Object message, boolean sent) throws RemotingException {
		if (isClosed()) {
			throw new RemotingException(this, "Failed to send message "
					+ (message == null ? "" : message.getClass().getName())
					+ ":" + message + ", cause: Channel closed. channel: "
					+ getLocalAddress() + " -> " + getRemoteAddress());
		}
	}

	@Override
	public String toString() {
		return getLocalAddress() + " -> " + getRemoteAddress();
	}
}

