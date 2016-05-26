package org.hdl.hpgsc.demo.helloworld.server;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hdl.hggsc.rpc.channel.ExchangeChannel;
import org.hdl.hggsc.rpc.protocol.common.StringParam;
import org.hdl.hggsc.rpc.service.annotation.RemoteService;
import org.hdl.hggsc.rpc.service.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author qiuhd
 *
 */
@RemoteService
public class DemoService {

	Logger logger = LoggerFactory.getLogger(DemoService.class);
	/**
	 * 
	 * @param name
	 * @return
	 */
	@RequestMapping(id=100,param=StringParam.class)
	public StringParam sayHello(StringParam param,ExchangeChannel channel) {
		String name = param.getValue();
        System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] Hello " + name + ", request from consumer: " + channel.getRemoteAddress());
        return new StringParam("Hello " + name + ", response form provider: " + channel.getLocalAddress());
    }
}
