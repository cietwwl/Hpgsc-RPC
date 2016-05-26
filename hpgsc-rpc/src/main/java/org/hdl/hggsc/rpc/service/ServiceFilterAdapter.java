package org.hdl.hggsc.rpc.service;

import org.hdl.hggsc.rpc.channel.ExchangeChannel;
import org.hdl.hggsc.rpc.protocol.NetRequest;
/**
 * ServiceFilterAdapter
 * 服务过滤器适配器
 * @author qiuhd
 *
 */
public class ServiceFilterAdapter implements ServiceFilter {

	@Override
	public boolean onPre(NetRequest request,ExchangeChannel channle,Object handler) throws Exception {
		return true;
	}

	@Override
	public void onPost(NetRequest request, ExchangeChannel channel,Object handler, Object result) throws Exception {
		
	}
}
