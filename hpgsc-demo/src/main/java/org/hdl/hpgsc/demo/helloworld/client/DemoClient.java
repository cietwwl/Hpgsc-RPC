package org.hdl.hpgsc.demo.helloworld.client;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hdl.hggsc.rpc.client.HpgscClient;
import org.hdl.hggsc.rpc.protocol.common.StringParam;
/**
 * @author qiuhd
 */
public class DemoClient {

	public static void main(String[] args) throws InterruptedException {
		HpgscClient client = HpgscClient.build("test", "172.19.40.23",9090,1000000);
		client.connect();
		
		for (int i = 0; i < Integer.MAX_VALUE; i ++) {
            try {
            	StringParam param = new StringParam("world" + i);
            	StringParam hello = client.syncRequest(100, param, StringParam.class);
                System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + hello.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(2000);
        }
	}
	
}
