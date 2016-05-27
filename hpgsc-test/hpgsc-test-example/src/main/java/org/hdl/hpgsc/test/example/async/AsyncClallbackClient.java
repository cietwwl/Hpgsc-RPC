package org.hdl.hpgsc.test.example.async;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hdl.hggsc.rpc.client.HpgscClient;
import org.hdl.hggsc.rpc.client.ResponseCallback;
import org.hdl.hggsc.rpc.protocol.common.StringParam;

/**
 * 
 * @author qiuhd
 *
 */
public class AsyncClallbackClient {

	public static void main(String[] args) throws InterruptedException {
		HpgscClient client = HpgscClient.build("async-test-client", "172.17.32.124", 9090, 3000);
		
		client.connect();
		
		for (int i = 0; i < Integer.MAX_VALUE; i ++) {
            try {
            	StringParam param = new StringParam("world" + i);
            	client.asyncRequest(100, param, StringParam.class,CALLBACK);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(2000);
        }
	}
	
	private static final ResponseCallback<StringParam> CALLBACK = new ResponseCallback<StringParam>() {

		public void onResponse(StringParam result) {
            System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + result.getValue());
		}

		public void onException(Throwable cause) {
			cause.printStackTrace();
		}
	};
}
