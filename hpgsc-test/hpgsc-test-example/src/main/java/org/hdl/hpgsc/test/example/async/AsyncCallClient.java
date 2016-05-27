package org.hdl.hpgsc.test.example.async;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hdl.hggsc.rpc.client.HpgscClient;
import org.hdl.hggsc.rpc.client.ResponseFuture;
import org.hdl.hggsc.rpc.protocol.common.StringParam;

/**
 * 
 * @author public
 *
 */
public class AsyncCallClient {

	public static void main(String[] args) throws InterruptedException {
		
		HpgscClient client = HpgscClient.build("async-test-client", "172.17.32.124", 9090, 3000);
		
		client.connect();
		
		for (int i = 0; i < Integer.MAX_VALUE; i ++) {
            try {
            	StringParam param = new StringParam("world" + i);
            	ResponseFuture<StringParam> future = client.asyncRequest(100, param, StringParam.class);
            	StringParam result = future.get();
                System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + result.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(2000);
        }
	}
}
