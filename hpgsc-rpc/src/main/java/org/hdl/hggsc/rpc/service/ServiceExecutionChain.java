package org.hdl.hggsc.rpc.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hdl.hggsc.rpc.channel.ExchangeChannel;
import org.hdl.hggsc.rpc.protocol.NetRequest;
import org.hdl.hpgsc.common.utils.Preconditions;
/**
 * ServiceExecutionChain.
 * @author qiuhd
 * @since  2015年9月22日
 * @version V1.0.0
 */
public class ServiceExecutionChain {

	private final Object handler;
	
	private ServiceFilter[] filters;

	private List<ServiceFilter> filterList;

	public ServiceExecutionChain(Object handler) {
		Preconditions.checkArgument(handler != null,"handler is require");
		this.handler = handler;
	}
	
	public void addFilter(ServiceFilter interceptor) {
		initFilterList();
		this.filterList.add(interceptor);
	}

	public void addFilters(ServiceFilter[] filters) {
		if (filters != null) {
			initFilterList();
			this.filterList.addAll(Arrays.asList(filters));
		}
	}

	private void initFilterList() {
		if (this.filterList == null) {
			this.filterList = new ArrayList<ServiceFilter>();
		}
		if (this.filters != null) {
			this.filterList.addAll(Arrays.asList(this.filters));
			this.filters = null;
		}
	}
	
	/**
	 * Return the array of filters to apply (in the given order).
	 * @return the array of ServiceFilter instances (may be {@code null})
	 */
	public ServiceFilter[] getFilters() {
		if (this.filters == null && this.filterList != null) {
			this.filters = this.filterList.toArray(new ServiceFilter[this.filterList.size()]);
		}
		return this.filters;
	}
	
	/**
	 * Apply onPre methods of registered filters.
	 * @return {@code true} if the execution chain should proceed with the
	 * next filter or the handler itself. Else, DispatcherServlet assumes
	 * that this filter has already dealt with the response itself.
	 */
	boolean applyPreHandle(NetRequest request,ExchangeChannel channle) throws Exception {
		if (getFilters() != null) {
			for (int i = 0; i < getFilters().length; i++) {
				ServiceFilter interceptor = getFilters()[i];
				if (!interceptor.onPre(request,channle, this.handler)) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Apply onPost methods of registered filters.
	 */
	void applyPostHandle(NetRequest request, ExchangeChannel channle,Object result) throws Exception {
		if (getFilters() == null) {
			return;
		}
		for (int i = getFilters().length - 1; i >= 0; i--) {
			ServiceFilter interceptor = getFilters()[i];
			interceptor.onPost(request, channle, this.handler, result);
		}
	}
	
	public Object getHandler() {
		return handler;
	}
	
	/**
	 * Delegates to the handler's {@code toString()}.
	 */
	@Override
	public String toString() {
		if (this.handler == null) {
			return "serviceExecutionChain with no handler";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("ServiceExecutionChain with handler [").append(this.handler).append("]");
		if (this.filterList != null && this.filterList.size() > 0) {
			sb.append(" and ").append(this.filterList.size()).append(" interceptor");
			if (this.filterList.size() > 1) {
				sb.append("s");
			}
		}
		return sb.toString();
	}
	
}
