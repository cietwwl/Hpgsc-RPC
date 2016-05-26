package org.hdl.hpgsc.demo.helloworld.server;

import org.hdl.hggsc.rpc.server.HpgscServer;
/**
 * 
 * @author qiuhd
 *
 */
public class DemoServer {
	
	public static void main(String[] args) {
		HpgscServer server = HpgscServer.build("testserver","172.19.40.23",9090);
		server.getConf().setReceiveBuff(1024 * 50 * 2);
		server.registryService(new DemoService());
		server.start();
	}
}
