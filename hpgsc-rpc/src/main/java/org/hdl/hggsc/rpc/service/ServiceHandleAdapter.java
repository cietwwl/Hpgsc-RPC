package org.hdl.hggsc.rpc.service;

import org.hdl.hggsc.rpc.channel.ExchangeChannel;
import org.hdl.hggsc.rpc.protocol.NetRequest;

/**
 * ServiceHandleAdapter
 * @author qiuhd
 *
 */
public interface ServiceHandleAdapter {
	
	Object handle(NetRequest requset,ExchangeChannel channel, Object handler) throws Exception;

}
