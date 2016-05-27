package org.hdl.hpgsc.test.example.event;

import org.hdl.hggsc.rpc.server.HpgscServer;

/**
 * 
 * @author qiuhd
 *
 */
public class EventServer {

	public static void main(String[] args) {
		HpgscServer server = HpgscServer.build("SeverEvent-test-server", "172.17.32.124", 9090);
		server.registryService(new EventService());
		server.start();
	}

}
