package org.hdl.hggsc.rpc.service;


import java.util.ArrayList;
import java.util.List;

import org.hdl.hpgsc.common.utils.Preconditions;
/**
 * InterceptorRegistry
 * @author qiuhd
 *
 */
public class FilterRegistry {

	private final List<ServiceFilter> adaptedFilters = new ArrayList<ServiceFilter>();
	
	private final List<MappedFilter> mappedFilters = new ArrayList<MappedFilter>();
	
	public void registry(Object filter) {
		Preconditions.checkArgument(filter != null, "filter can not be null!!");
		if (filter instanceof MappedFilter) {
			mappedFilters.add((MappedFilter)filter);
		}else if (filter instanceof ServiceFilter){
			adaptedFilters.add((ServiceFilter)filter);
		}else {
			throw new IllegalArgumentException("Failed to regist filter,Cause the filter not math!!!");
		}
	}
	
	protected final ServiceFilter[] getAdaptedFilters() {
		int count = adaptedFilters.size();
		return (count > 0) ? adaptedFilters.toArray(new ServiceFilter[count]) : null;
	}

	/**
	 * Return all configured {@link MappedFilter}s as an array.
	 * @return the array of {@link MappedFilter}s, or {@code null} if none
	 */
	protected final MappedFilter[] getMappedFilters() {
		int count = mappedFilters.size();
		return (count > 0) ? mappedFilters.toArray(new MappedFilter[count]) : null;
	}
}
