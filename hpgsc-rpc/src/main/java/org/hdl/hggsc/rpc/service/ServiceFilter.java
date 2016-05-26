package org.hdl.hggsc.rpc.service;

import org.hdl.hggsc.rpc.channel.ExchangeChannel;
import org.hdl.hggsc.rpc.protocol.NetRequest;
/**
 * ServiceFilter
 * 服务拦截器
 * @author qiuhd
 */
public interface ServiceFilter {
	/**
	 * 执行服务之前调用该方法
	 * @param request
	 * @param response
	 * @param channle
	 * @param handler
	 * @return
	 * @throws Exception
	 */
	public boolean onPre(NetRequest request,ExchangeChannel channel,Object handler) throws Exception;
	/**
	 * 执行服务之后调用该方法
	 * @param request
	 * @param channle
	 * @param handler
	 * @param result
	 * @throws Exception
	 */
	public void onPost(NetRequest request,ExchangeChannel channel,Object handler,Object result) throws Exception ;
}
