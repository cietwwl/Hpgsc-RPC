package org.hdl.hpgsc.test.benchmark;

import org.hdl.hggsc.rpc.service.annotation.RemoteService;
import org.hdl.hggsc.rpc.service.annotation.RequestMapping;


/**
 * TODO Comment of HelloService
 * 
 * @author tony.chenl
 */
@RemoteService
public class DemoService {
	
	@RequestMapping(id=100,param=RequestObject.class)
	public RequestObject sendRequest(RequestObject request) {
		return request;
	}
	
	@RequestMapping(id=101,param=DemoRequest.class)
	public DemoRequest sendRequest(DemoRequest request) {
		return request;
	}
}
