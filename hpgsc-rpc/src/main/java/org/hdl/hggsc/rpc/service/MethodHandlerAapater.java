package org.hdl.hggsc.rpc.service;

import org.hdl.hggsc.rpc.channel.ExchangeChannel;
import org.hdl.hggsc.rpc.protocol.NetRequest;
import org.hdl.hggsc.rpc.service.method.InvocableMethodHandler;
import org.hdl.hggsc.rpc.service.method.MethodHandler;

/**
 * MethodHandlerAapater.
 * @author qiuhd
 * @since  2014年9月26日
 * @version V1.0.0
 */
public class MethodHandlerAapater implements ServiceHandleAdapter {

	@Override
	public Object handle(NetRequest request,ExchangeChannel channle, Object handler) throws Exception {
		return handleInternal(request,channle, (MethodHandler)handler);
	}
	
	private Object handleInternal(NetRequest request,ExchangeChannel channle, MethodHandler handler) throws Exception{
		InvocableMethodHandler invokHandler = new InvocableMethodHandler(handler);
		Object returnValue = invokHandler.invokeForRequest(request,channle);
		return returnValue;
	}
}
