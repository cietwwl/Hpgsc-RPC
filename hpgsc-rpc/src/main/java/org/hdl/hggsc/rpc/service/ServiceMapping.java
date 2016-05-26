package org.hdl.hggsc.rpc.service;

import org.hdl.hggsc.rpc.protocol.NetRequest;

/**
 * ServiceMapping
 * @author qiuhd
 *
 */
public interface ServiceMapping {
	
	ServiceExecutionChain getHandler(NetRequest request);
	
}
