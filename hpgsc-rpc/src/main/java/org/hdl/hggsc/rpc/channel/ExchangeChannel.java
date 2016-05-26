package org.hdl.hggsc.rpc.channel;

import java.net.InetSocketAddress;

import org.hdl.hggsc.rpc.protocol.NetEvent;
import org.hdl.hggsc.rpc.server.HpgscServer;
import org.hdl.hpgsc.common.io.Record;
import org.hdl.hpgsc.remoting.Channel;
/**
 * ExchangeChannel
 * 
 * @author qiuhd
 */
public final class ExchangeChannel {

	private static final String CHANNEL_KEY = ExchangeChannel.class.getName() + ".CHANNEL";
	
	private final Channel channel;
	/**
	 * channel 唯一 身份
	 */
	private String identify;
	/**
	 * Channel 唯一索引
	 */
	private int index;
	
	private HpgscServer server;
	
	private ExchangeChannel(Channel channel,HpgscServer server) {
		this.channel = channel;
		this.server = server;
	}

	public static ExchangeChannel getOrAddChannel(Channel ch,HpgscServer server) {
		if (ch == null || server == null) {
			return null;
		}
		ExchangeChannel ret = (ExchangeChannel) ch.getAttribute(CHANNEL_KEY);
		if (ret == null) {
			ret = new ExchangeChannel(ch,server);
			if (ch.isConnected()) {
				ch.setAttribute(CHANNEL_KEY, ret);
			}
		}
		return ret;
	}
	
	public static void removeChannelIfDisconnected(Channel ch) {
		if (ch != null && !ch.isConnected()) {
			ch.removeAttribute(CHANNEL_KEY);
		}
	}
	
	public void push(long id,Record parame) {
		try {
			NetEvent event = new NetEvent(id);
			event.setSource(server.getIdentify());
			event.setContent(parame);
			channel.send(event);
		}catch(Throwable t){
			throw new IllegalStateException("Failed to push message",t);
		}
	}
	
	public boolean isConnected() {
		return this.isConnected();
	}
	
	public String getIdentify() {
		return identify;
	}

	public int getIndex() {
		return index;
	}
	
	public void setIdentify(String identify) {
		this.identify = identify;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public InetSocketAddress getRemoteAddress() {
		return channel.getRemoteAddress();
	}
	
	public InetSocketAddress getLocalAddress() {
		return server.getConf().getLocalAddress();
	}
	/**
	 * 返回指定key的属性值
	 * @param key
	 * @return
	 */
	public Object getAttribute(String key) {
		return this.channel.getAttribute(key);
	}
	/**
	 * 设置属性
	 * @param key
	 * @param object
	 * @return
	 */
	public void setAttribute(String key,Object object) {
		this.channel.setAttribute(key, object);
	}
	/**
	 * Return true if contain key
	 * @param key
	 * @return
	 */
	public boolean contains(String key) {
		return this.channel.contains(key);
	}
	
    /**
     * Remove attribute.
     * @param key key.
     */
	public void removeAttribute(String key) {
		this.channel.removeAttribute(key);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((identify == null) ? 0 : identify.hashCode());
		result = prime * result + index;
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
		ExchangeChannel other = (ExchangeChannel) obj;
		if (identify == null) {
			if (other.identify != null)
				return false;
		} else if (!identify.equals(other.identify))
			return false;
		if (index != other.index)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ExchangeChannel [channel=" + channel + ", identify=" + identify
				+ ", index=" + index + "]";
	}
}
