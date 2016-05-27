package org.hdl.hpgsc.test.example.event;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hdl.hggsc.rpc.client.HpgscClient;
import org.hdl.hggsc.rpc.client.ResponseFuture;
import org.hdl.hggsc.rpc.client.event.ServerEventListener;
import org.hdl.hggsc.rpc.protocol.NetEvent;
import org.hdl.hggsc.rpc.protocol.common.StringParam;

/**
 * 
 * @author public
 *
 */
public class EventClient2 {

	public static void main(String[] args) throws InterruptedException {
		HpgscClient client = HpgscClient.build("ServerEvent-test-client2", "172.17.32.124", 9090, 3000);
		client.registerListener(101, StringParam.class, LISTENER);
		client.connect();
		
		for (int i = 0; i < Integer.MAX_VALUE; i ++) {
            try {
            	StringParam param = new StringParam("client2-world" + i);
            	client.notify(100, param);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(2000);
        }
	}
	
	public static final ServerEventListener LISTENER = new ServerEventListener() {
		
		public void handleEvent(NetEvent event) {
			StringParam result = event.getParame(StringParam.class);
            System.out.println("Source: " + event.getSource() + " [" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + result.getValue());
		}
	};
}
