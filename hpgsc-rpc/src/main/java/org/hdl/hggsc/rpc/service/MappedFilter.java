package org.hdl.hggsc.rpc.service;
/**
 * MappedFilter.
 * @author qiuhd
 * @since  2014年11月4日
 * @version V1.0.0
 */
public class MappedFilter {

	private final long[] includePatterns;

	private final long[] excludePatterns;

	private final ServiceFilter filter;

	/**
	 * Create a new MappedFilter instance.
	 * @param includePatterns the path patterns to map with a {@code null} value matching to all paths
	 * @param interceptor the ServiceFilter instance to map to the given patterns
	 */
	public MappedFilter(long[] includePatterns, ServiceFilter filter) {
		this(includePatterns, null, filter);
	}

	/**
	 * Create a new MappedFilter instance.
	 * @param includePatterns the path patterns to map with a {@code null} value matching to all paths
	 * @param excludePatterns the path patterns to exclude
	 * @param interceptor the ServiceFilter instance to map to the given patterns
	 */
	public MappedFilter(long[] includePatterns, long[] excludePatterns, ServiceFilter interceptor) {
		this.includePatterns = includePatterns;
		this.excludePatterns = excludePatterns;
		this.filter = interceptor;
	}

	/**
	 * The path into the application the filter is mapped to.
	 */
	public long[] getPathPatterns() {
		return this.includePatterns;
	}

	/**
	 * The actual filter reference.
	 */
	public ServiceFilter getInterceptor() {
		return this.filter;
	}

	/**
	 * Returns {@code true} if the filter applies to the given request path.
	 * @param lookupPath the current request path
	 * @param pathMatcher a path matcher for path pattern matching
	 */
	public boolean matches(long lookupPath) {
		if (this.excludePatterns != null) {
			for (long path : this.excludePatterns) {
				if (path == lookupPath) {
					return false;
				}
			}
		}
		if (this.includePatterns == null) {
			return true;
		}
		else {
			for (long path : this.includePatterns) {
				if (path == lookupPath) {
					return true;
				}
			}
			return false;
		}
	}
}
