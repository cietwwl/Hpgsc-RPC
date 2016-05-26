package org.hdl.hggsc.rpc.service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import org.hdl.hggsc.rpc.protocol.NetRequest;
import org.hdl.hggsc.rpc.server.HpgscServer;
import org.hdl.hggsc.rpc.service.annotation.RemoteService;
import org.hdl.hggsc.rpc.service.annotation.RequestMapping;
import org.hdl.hggsc.rpc.service.method.MethodHandler;
import org.hdl.hggsc.rpc.service.method.MethodHandlerSelector;
import org.hdl.hggsc.rpc.utils.AnnotationUtils;
import org.hdl.hggsc.rpc.utils.ReflectionUtils.MethodFilter;
import org.hdl.hpgsc.common.io.Record;
import org.hdl.hpgsc.common.utils.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ServcieLocator
 * @author qiuhd
 *
 */
public class ServiceLocator {
	
	private static final Logger LOG = LoggerFactory.getLogger(ServiceLoader.class);
	private Map<Long, ServiceEntity> serviceEntitys = new HashMap<Long, ServiceEntity>();
	private Map<ServiceEntity, MethodHandler> methodHandlers = new LinkedHashMap<ServiceEntity, MethodHandler>();
	private final FilterRegistry filterRegistry;
	
	public ServiceLocator(HpgscServer server) {
		this.filterRegistry = server.getFilterRegistry();
	}
	
	/**
	 * 注入服务
	 * 
	 * @param key
	 * @param serviceBean
	 * @return
	 */
	public boolean regiserService(final Object serviceBean) {
		Preconditions.checkArgument(serviceBean != null,"serviceBean can not be null!!");
		Class<?> serviceClass = serviceBean.getClass();
		RemoteService remoteService = AnnotationUtils.findAnnotation(serviceClass, RemoteService.class);
		if (remoteService == null) {
			LOG.warn("Unfound "+ RemoteService.class.getName() + " annotation in " + serviceClass.getName());
		}
		final Map<Method, ServiceEntity> mappings = new IdentityHashMap<Method, ServiceEntity>();
		Set<Method> methods = MethodHandlerSelector.selectMethods(serviceClass,
				new MethodFilter() {
					@Override
					public boolean matches(Method method) {
						ServiceEntity mapping = getMappingForMethod(method,serviceBean);
						if (mapping != null) {
							mappings.put(method, mapping);
							return true;
						}
						return false;
					}
				});
		for (Method method : methods) {
			registerHandlerMethod(serviceBean, method, mappings.get(method));
		}
		return true;
	}
	
	/**
	 * 返回服务实体
	 * @param request
	 * @return
	 */
	public ServiceEntity getServiceEntity(NetRequest request) {
		long serviceId = request.getId();
		return this.serviceEntitys.get(serviceId);
	}
	
	/**
	 * 返回服务实体
	 * @param serviceId
	 * @return
	 */
	public ServiceEntity getServiceEntity(long serviceId) {
		return this.serviceEntitys.get(serviceId);
	}
	
	/**
	 * Support request
	 * @param request
	 * @return
	 */
	public boolean supportRequest(NetRequest request) {
		ServiceEntity serviceEntity = serviceEntitys.get(request.getId());
		return serviceEntity != null ? true : false;
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public ServiceExecutionChain getHandler(NetRequest request) {
		MethodHandler methodHandler = getHandlerInternal(request);
		
		if (methodHandler == null) 
			return null;
		ServiceExecutionChain chain = new ServiceExecutionChain(methodHandler);
		chain.addFilters(filterRegistry.getAdaptedFilters());

		long serviceId = request.getId();
		
		MappedFilter[] mappedInterceptors = filterRegistry.getMappedFilters();
		if (mappedInterceptors != null) {
			for (MappedFilter mappedInterceptor : mappedInterceptors) {
				if (mappedInterceptor.matches(serviceId)) {
					chain.addFilter(mappedInterceptor.getInterceptor());
				}
			}
		}
		return chain;
	}

	private MethodHandler getHandlerInternal(NetRequest request) {
		long serviceId = request.getId();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Looking up handler method for id " + serviceId);
		}
		ServiceEntity requestMappingInfo = serviceEntitys.get(serviceId);
		if (requestMappingInfo == null) {
			return null;
		}
		
		MethodHandler methodHandler = methodHandlers.get(requestMappingInfo);
		if (LOG.isDebugEnabled()) {
			if (methodHandler != null) {
				LOG.debug("Returning handler method [" + methodHandler + "]");
			}
			else {
				LOG.debug("Did not find handler method for [" + serviceId + "]");
			}
		}
		return (methodHandler != null ? methodHandler.createWithResolvedBean() : null);
	}
    
	protected void registerHandlerMethod(Object handler, Method method, ServiceEntity serviceDescribtor) {
		MethodHandler newHandlerMethod = new MethodHandler(handler, method);
		MethodHandler oldHandlerMethod = this.methodHandlers.get(serviceDescribtor);
		if (oldHandlerMethod != null && !oldHandlerMethod.equals(newHandlerMethod)) {
			throw new IllegalStateException("Ambiguous mapping found. Cannot map '" + newHandlerMethod.getBean() +
					"' bean method \n" + newHandlerMethod + "\nto " + serviceDescribtor + ": There is already '" +
					oldHandlerMethod.getBean() + "' bean method\n" + oldHandlerMethod + " mapped.");
		}
		
		this.methodHandlers.put(serviceDescribtor, newHandlerMethod);
		if (LOG.isInfoEnabled()) {
			LOG.info("Mapped \"" + serviceDescribtor + "\" onto " + newHandlerMethod);
		}
		long serviceId = serviceDescribtor.getServiceId();
		serviceEntitys.put(serviceId,serviceDescribtor);
	}
	
    private ServiceEntity getMappingForMethod(Method method, Object serviceBean) {
		ServiceEntity serviceEntity = null;
		RequestMapping methodAnnotation = AnnotationUtils.findAnnotation(method, RequestMapping.class);
		if (methodAnnotation != null) {
			long serviceId = methodAnnotation.id();
			Class<? extends Record>[] param = methodAnnotation.param();
			Class<? extends ServiceFilter>[] filterClazzs = methodAnnotation.filter();
			for (Class<? extends ServiceFilter> filterClazz : filterClazzs) {
				ServiceFilter filter = null;
				try {
					filter = filterClazz.newInstance();
				}catch(Exception e) {
					LOG.error("Instance ServcieFilter error",e);
					continue;
				}
				long[] includePatterns = new long[] {serviceId};
				MappedFilter mappedFilter = new MappedFilter(includePatterns, filter);
				filterRegistry.registry(mappedFilter);
			}
			Class<?> returnClass = method.getReturnType();
			serviceEntity = new ServiceEntity(serviceId);
			serviceEntity.setServiceBean(serviceBean).setMethod(method);
			if (param != null) {
				serviceEntity.setParamClass(param[0]);
			}
			serviceEntity.setReturnClass(returnClass);
		}
		return serviceEntity;
	}
    
    
    
}
