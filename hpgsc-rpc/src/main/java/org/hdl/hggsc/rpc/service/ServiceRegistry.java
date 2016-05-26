package org.hdl.hggsc.rpc.service;

import java.util.HashMap;
import java.util.Map;

import org.hdl.hpgsc.common.utils.Preconditions;

/**
 * ServiceRegistry
 * @author qiuhd
 *
 */
public class ServiceRegistry {
	
	private Map<Long, ServiceEntity> serviceEntities = new HashMap<Long, ServiceEntity>();
	
	public void registry(long serviceId,ServiceEntity entity){
		Preconditions.checkArgument(entity != null, "entity can not be null!!");
		if (serviceEntities.containsKey(serviceId)) {
			throw new IllegalArgumentException("Failed to registry service,Cause of the Service id (" + serviceId + ") was already registed.");
		}
		serviceEntities.put(serviceId, entity);
	}
	
	public ServiceEntity getServiceEntify(long servcieId) {
		return this.serviceEntities.get(servcieId);
	}
}
