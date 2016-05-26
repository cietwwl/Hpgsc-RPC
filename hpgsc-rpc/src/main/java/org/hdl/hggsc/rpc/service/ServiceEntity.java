package org.hdl.hggsc.rpc.service;

import java.lang.reflect.Method;

import org.hdl.hpgsc.common.io.Record;

/**
 * ServiceDescribtor
 * @author qiuhd
 * 
 */
public class ServiceEntity {
	
	/**
	 * 服务的id，唯一标识
	 */
	private long serviceId;
	/**
	 * 服务对应的method
	 */
	private Method method;
	/**
	 * 服务具体的实现bean对象
	 */
	private Object serviceBean;
	/**
	 * 服务输入参数对象
	 */
	private Class<? extends Record> paramClass;
	/**
	 * 服务返回的对象
	 */
	private Class<?> returnClass;
	
	public ServiceEntity(long serviceId) {
		this.serviceId = serviceId;
	}
	
	public ServiceEntity setServiceId(long serviceId) {
		this.serviceId = serviceId;
		return this;
	}
	
	public long getServiceId() {
		return this.serviceId;
	}
	
	public ServiceEntity setMethod(Method method) {
		this.method = method;
		return this;
	}
	
	public Method getMethod() {
		return this.method;
	}
	
	public ServiceEntity setServiceBean(Object serviceBean) {
		this.serviceBean = serviceBean;
		return this;
	}
	
	public Object getServiceBean() {
		return this.serviceBean;
	}
	
	public ServiceEntity setParamClass(Class<? extends Record> paramClass) {
		this.paramClass = paramClass;
		return this;
	}
	
	public Class<? extends Record> getParamClass() {
		return this.paramClass;
	}
	
	public ServiceEntity setReturnClass(Class<?> returnClass) {
		this.returnClass = returnClass;
		return this;
	}
	
	public Class<?> getReturnClass() {
		return this.returnClass;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[ServiceEntity : {");
		builder.append(" ServiceId :").append(serviceId).append(",");
		builder.append(" ServiceBean :").append(serviceBean != null ? serviceBean.getClass().getSimpleName() : "N/A").append(",");
		builder.append(" Method :").append(method != null ? method.getName() : "N/A").append(",");
		builder.append(" ParamClass :").append(paramClass != null ? paramClass.getSimpleName() : "N/A").append(",");
		builder.append(" ReturnClass :").append(returnClass != null ? returnClass.getSimpleName() : "N/A");
		builder.append("}]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (serviceId ^ (serviceId >>> 32));
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
		ServiceEntity other = (ServiceEntity) obj;
		if (serviceId != other.serviceId)
			return false;
		return true;
	}
}
