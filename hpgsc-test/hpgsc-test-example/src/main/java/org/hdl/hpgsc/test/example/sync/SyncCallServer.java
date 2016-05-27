package org.hdl.hpgsc.test.example.sync;

import org.hdl.hggsc.rpc.server.HpgscServer;
import org.hdl.hpgsc.test.example.event.EventService;

/**
 * 
 * @author qiuhd
 *
 */
public class SyncCallServer {

	public static void main(String[] args) {
		HpgscServer server = HpgscServer.build("example-server", "172.17.32.124", 9090);
		server.registryService(new EventService());
		server.start();
	}

}
