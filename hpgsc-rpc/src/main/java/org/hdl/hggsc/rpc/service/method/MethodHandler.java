package org.hdl.hggsc.rpc.service.method;

import java.lang.reflect.Method;

import org.hdl.hpgsc.common.utils.Preconditions;
/**
 * MethodHandler .
 * @author qiuhd
 * @since  2014年9月23日
 * @version V1.0.0
 */
public class MethodHandler {

	private final Object bean;
	private final Method method;
	private final MethodParameter[] methodParameters;
	
	public MethodHandler(Object bean,Method method) {
		Preconditions.checkArgument(bean != null,"bean can not be null!");
		Preconditions.checkArgument(method != null,"method can not be null!");
		this.bean = bean;
		this.method = method ;
		this.methodParameters = initMethodParameters();
	}
	
	/**
	 * Re-create HandlerMethod with the resolved handler.
	 */
	public MethodHandler(MethodHandler methodHandler) {
		Preconditions.checkArgument(methodHandler != null, "methodHandler can not be null!");
		this.bean = methodHandler.bean;
		this.method = methodHandler.method;
		this.methodParameters = methodHandler.methodParameters;
	}
	
	private MethodParameter[] initMethodParameters() {
		int count = this.method.getParameterTypes().length;
		MethodParameter[] result = new MethodParameter[count];
		for (int i = 0; i < count; i++) {
			result[i] = new HandlerMethodParameter(i);
		}
		return result;
	}
	
	public Object getBean() {
		return bean;
	}

	public Method getMethod() {
		return method;
	}
	
	public String getMethodName(){
		return this.method.getName();
	}

	public MethodParameter[] getMethodParameters() {
		return methodParameters;
	}

	public Class<?> getBeanType() {
		return  this.bean.getClass();
	}
	
	public MethodHandler createWithResolvedBean() {
		return new MethodHandler(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj != null && obj instanceof MethodHandler) {
			MethodHandler other = (MethodHandler) obj;
			return (this.bean.equals(other.bean) && this.method.equals(other.method));
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.bean.hashCode() * 31 + this.method.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[ MethodHandler : {");
		builder.append(" ServiceBean :").append(bean != null ? bean.getClass().getSimpleName() : "N/A").append(",");
		builder.append(" Method :").append(method != null ? method.getName() : "N/A");
		builder.append("}]");
		return builder.toString();
	}
	
	/**
	 * A MethodParameter with HandlerMethod-specific behavior.
	 */
	private class HandlerMethodParameter extends MethodParameter {

		public HandlerMethodParameter(int index) {
			super(MethodHandler.this.method, index);
		}
	}
}
