package org.hdl.hpgsc.test.example.event;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hdl.hggsc.rpc.channel.ExchangeChannel;
import org.hdl.hggsc.rpc.channel.ExchangeChannels;
import org.hdl.hggsc.rpc.protocol.common.StringParam;
import org.hdl.hggsc.rpc.service.annotation.RemoteService;
import org.hdl.hggsc.rpc.service.annotation.RequestMapping;

/**
 * AsyncCallService
 * @author qiuhd
 *
 */
@RemoteService
public class EventService {
	/**
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(id=100,param=StringParam.class)
	public void asyncCall1(StringParam param,ExchangeChannel channel) {
		String name = param.getValue();
        System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] Hello " + name + ", request from consumer: " + channel.getRemoteAddress());
        StringParam result = new StringParam("Hello " + name + ", response form provider: " + channel.getLocalAddress());
//        channel.push(101, result);
//        ExchangeChannels.push(channel.getIdentify(), 101, result);
        ExchangeChannels.broadcast(101, result);
	}
}
