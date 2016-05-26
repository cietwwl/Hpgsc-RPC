package org.hdl.hggsc.rpc.channel;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.hdl.hpgsc.common.io.Record;
import org.hdl.hpgsc.common.utils.StringUtils;
/**
 * 
 * @author qiuhd
 */
public final class ExchangeChannels {

	private static final Map<String, Queue<ExchangeChannel>> CHANNEL_CACHE = new ConcurrentHashMap<String, Queue<ExchangeChannel>>();
	
	public static final ExchangeChannel getChannel(String identify) {
		Queue<ExchangeChannel> channels = CHANNEL_CACHE.get(identify);
		ExchangeChannel channel = null;
		if (channels != null) {
			while ((channel = channels.poll()) != null) {
				if (channel.isConnected()) {
					return channel;
				}
			}
		}
		return channel;
	}
	
	public static final Collection<ExchangeChannel> getChannels(String identify) {
		Queue<ExchangeChannel> channels = CHANNEL_CACHE.get(identify);
		if (channels != null) {
			return Collections.unmodifiableCollection(channels);
		}
		return Collections.emptyList();
	}
	
	public static final boolean addChannel(ExchangeChannel channel) {
		if (channel != null) {
			String identify = channel.getIdentify();
			if (StringUtils.isEmpty(identify)) {
				return false;
			}
			synchronized (identify.intern()) {
				Queue<ExchangeChannel> channels = CHANNEL_CACHE.get(identify);
				if (channels == null) {
					channels = new LinkedList<ExchangeChannel>();
					CHANNEL_CACHE.put(identify, channels);
				}
				channels.add(channel);
				return true;
			}
		}
		return false;
	}
	
	public static final void removeChannel(ExchangeChannel channel) {
		if (channel != null) {
			String identify = channel.getIdentify();
			if (!StringUtils.isEmpty(identify)) {
				Queue<ExchangeChannel> channels = CHANNEL_CACHE.get(identify);
				if (channels != null) {
					channels.remove(channel);
				}
			}
		}
	}
	
	public static final void push(String identify,long id,Record parame) {
		ExchangeChannel channel = getChannel(identify);
		if (channel != null) {
			channel.push(id, parame);
		}else {
			throw new IllegalStateException("Failed to push message ( id = " + id + ") due to Unfound channel :" + identify);
		}
	}
	
	public static final void pushAll(long id,Record parame) {
		Set<String> keySet = CHANNEL_CACHE.keySet();
		if (keySet != null) {
			for (String identify : keySet) {
				ExchangeChannel channel = getChannel(identify);
				channel.push(id, parame);
			}
		}
	}
}
