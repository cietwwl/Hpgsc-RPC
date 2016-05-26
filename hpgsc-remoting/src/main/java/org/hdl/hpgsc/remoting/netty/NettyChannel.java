package org.hdl.hpgsc.remoting.netty;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.hdl.hpgsc.Constants;
import org.hdl.hpgsc.common.Configuration;
import org.hdl.hpgsc.common.utils.Preconditions;
import org.hdl.hpgsc.remoting.ChannelHandler;
import org.hdl.hpgsc.remoting.RemotingException;
import org.hdl.hpgsc.remoting.support.AbstractChannel;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * NettyChannel.
 * @author qiuhd
 * @since  2014-8-1
 * @version V1.0.0
 */
public class NettyChannel extends AbstractChannel {
	
	private static final Logger logger = LoggerFactory.getLogger(NettyChannel.class);
	
	private static final ConcurrentMap<org.jboss.netty.channel.Channel, NettyChannel> channelMap = new ConcurrentHashMap<org.jboss.netty.channel.Channel, NettyChannel>();
	
	private final org.jboss.netty.channel.Channel channel;
	
	private final Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();
	
	public NettyChannel(Configuration conf,Channel channel,ChannelHandler handler) {
		super(conf, handler);
		
		Preconditions.checkArgument(channel != null,"Channel can not be null");
		
		this.channel = channel ;
	}

	static NettyChannel getOrAddChannel(Configuration conf,Channel channel, ChannelHandler handler) {
		if (channel == null) {
			return null;
		}
		NettyChannel ret = channelMap.get(channel);
		if (ret == null) {
			NettyChannel nc = new NettyChannel(conf, channel, handler);
			if (channel.isConnected()) {
				ret = channelMap.putIfAbsent(channel, nc);
			}
			if (ret == null) {
				ret = nc;
			}
		}
		return ret;
	}
	
	static void removeChannelIfDisconnected(org.jboss.netty.channel.Channel ch) {
		if (ch != null && !ch.isConnected()) {
			channelMap.remove(ch);
		}
	}
	
	@Override
	public boolean isConnected() {
		return channel.isConnected();
	}
	
	@Override
	public void close() {
		super.close();
		
		try {
            removeChannelIfDisconnected(channel);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            attributes.clear();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            channel.close();
            logger.debug("Close netty channel " + channel);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
	}
	
	public void send(Object message, boolean sent) throws RemotingException {
		super.send(message, sent);

		boolean success = true;
		int timeout = 0;
		try {
			ChannelFuture future = channel.write(message);
			if (sent) {
				timeout = conf.getInt(Constants.TIMEOUT_KEY, Constants.DEFAULT_TIMEOUT);
				success = future.await(timeout);
			}
			Throwable cause = future.getCause();
			if (cause != null) {
				throw cause;
			}
		} catch (Throwable e) {
			throw new RemotingException(this, "Failed to send message "
					+ message + " to " + getRemoteAddress() + ", cause: "
					+ e.getMessage(), e);
		}

		if (!success) {
			throw new RemotingException(this, "Failed to send message "
					+ message + " to " + getRemoteAddress() + "in timeout("
					+ timeout + "ms) limit");
		}
	}


	@Override
	public void setAttribute(String key, Object value) {
		if (value == null) { 
			attributes.remove(key);
		} else {
			attributes.put(key, value);
		}
	}

	@Override
	public void removeAttribute(String key) {
		attributes.remove(key);
	}

	@Override
	public Object getAttribute(String key) {
		return attributes.get(key);
	}
	
	@Override
	public boolean contains(String key) {
		return attributes.containsKey(key);
	}

	@Override
	public InetSocketAddress getLocalAddress() {
		return (InetSocketAddress) this.channel.getLocalAddress();
	}
	
	@Override
	public InetSocketAddress getRemoteAddress() {
		return (InetSocketAddress) channel.getRemoteAddress();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((channel == null) ? 0 : channel.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NettyChannel other = (NettyChannel) obj;
		if (channel == null) {
			if (other.channel != null)
				return false;
		} else if (!channel.equals(other.channel))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NettyChannel [channel=" + channel + "]";
	}
}

