package org.hdl.hggsc.rpc.filter;

import org.hdl.hggsc.rpc.channel.ExchangeChannel;
import org.hdl.hggsc.rpc.protocol.NetRequest;
import org.hdl.hggsc.rpc.service.ServiceFilterAdapter;
import org.hdl.hggsc.rpc.service.method.MethodHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 服务超时过滤器
 * @author qiuhd
 *
 */
public class TimeoutFilter extends ServiceFilterAdapter{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TimeoutFilter.class);
	
	private ThreadLocal<Long> threadLocal = new ThreadLocal<Long>();
	
	private static final int DEFAULT_TIMEOUT = 1000;
	
	private int timeout = DEFAULT_TIMEOUT;
	
	public TimeoutFilter() {}
	
	public TimeoutFilter(int timeout){
		this.timeout = timeout;
	}
	
	@Override
	public boolean onPre(NetRequest request,ExchangeChannel channle, Object handler) throws Exception {
		threadLocal.set(System.currentTimeMillis());
		return super.onPre(request, channle, handler);
	}

	@Override
	public void onPost(NetRequest request, ExchangeChannel channle,Object handler, Object result) throws Exception {
		long preTime = threadLocal.get();
		long curTime = System.currentTimeMillis();
		long elapsed = curTime - preTime;
		if (elapsed > timeout){
			MethodHandler methodHandler = (MethodHandler) handler;
			LOGGER.warn("Invoke timeout ," + methodHandler.toString() + ", invoke elapsed " + elapsed + " ms.");
		}
	}
}
