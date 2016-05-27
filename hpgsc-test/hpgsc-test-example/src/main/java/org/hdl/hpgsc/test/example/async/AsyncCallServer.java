package org.hdl.hpgsc.test.example.async;

import org.hdl.hggsc.rpc.server.HpgscServer;

/**
 * 
 * @author qiuhd
 *
 */
public class AsyncCallServer {

	public static void main(String[] args) {
		HpgscServer server = HpgscServer.build("example-server", "172.17.32.124", 9090);
		server.registryService(new AsyncCallService());
		server.start();
	}

}
