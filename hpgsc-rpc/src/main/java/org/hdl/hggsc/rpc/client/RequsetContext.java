package org.hdl.hggsc.rpc.client;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hdl.hggsc.rpc.exception.RpcException;
import org.hdl.hggsc.rpc.exception.TimeoutException;
import org.hdl.hggsc.rpc.protocol.NetRequest;
import org.hdl.hggsc.rpc.protocol.NetResponse;
import org.hdl.hggsc.rpc.protocol.NetResponse.ErrorCode;
import org.hdl.hpgsc.common.io.Record;
import org.hdl.hpgsc.common.utils.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author qiuhd
 *
 */
public class RequsetContext {
	
	private static final Logger logger = LoggerFactory.getLogger(RequsetContext.class);
	
	private static final Map<Long, RequsetContext> CONTEXT_CACHE = new ConcurrentHashMap<Long, RequsetContext>();
	/**
	 * 请求随机序列
	 */
	private final long sequence;
	/**
	 * 开始请求时间
	 */
	private long requestTimestamp = System.currentTimeMillis();
	/**
	 * Client
	 */
	private final IClient client;
	/**
	 * 响应超时
	 */
	private final int timeout;
	
	private ResponseCallback<? extends Record> callback;
	
	private Class<? extends Record> responseClass;
	
	private final NetRequest request;
	
	public RequsetContext(long sequence,IClient client,NetRequest request,int timeout) {
		this.sequence = sequence;
		this.client = client;
		this.request = request;
		this.timeout = timeout;
		CONTEXT_CACHE.put(sequence, this);
	}

	public IClient getClient() {
		return client;
	}

	public int getTimeout() {
		return timeout;
	}

	public long getSequence() {
		return sequence;
	}

	public ResponseCallback<? extends Record> getCallback() {
		return callback;
	}

	public Class<? extends Record> getResponseClass() {
		return responseClass;
	}

	public void setCallback(ResponseCallback<? extends Record> callback) {
		this.callback = callback;
	}

	public void setResponseClass(Class<? extends Record> responseClass) {
		this.responseClass = responseClass;
	}

	public long getRequestTimestamp() {
		return requestTimestamp;
	}

	public void setRequestTimestamp(long requestTimestamp) {
		this.requestTimestamp = requestTimestamp;
	}

	public NetRequest getRequest() {
		return request;
	}
	
	public static void add(long sequence,RequsetContext context) {
		Preconditions.checkArgument(context != null, "context must be not null!!");
		CONTEXT_CACHE.put(sequence, context);
	}
		
	public static RequsetContext get(long sequence) {
		return CONTEXT_CACHE.get(sequence);
	}
	
	public static void remove(long sequence) {
		CONTEXT_CACHE.remove(sequence);
	}
	
	public static Class<? extends Record> getResponseClass(long sequence) {
		RequsetContext context = get(sequence);
		return context.getResponseClass();
	}
	
	@SuppressWarnings("unchecked")
	public static void handleReponse(IClient client,NetResponse response) {
		long sequence = response.getSequence();
		try {
			RequsetContext context = get(sequence);
			if (context != null) {
				ResponseCallback<Record> callback = (ResponseCallback<Record>) context.getCallback();
				if (response.isOK()) {
					Record result = (Record) response.getContent();
					try {
						callback.onResponse(result);
					} catch (Exception e) {
						 logger.error("callback invoke error .reasult:" + result + ",client :" + client, e);
					}
				}else if (response.getErrorCode() == ErrorCode.SERVER_TIMEOUT.getValue()) {
					try {
						TimeoutException te = new TimeoutException(response.getErrorDes());
						callback.onException(te);
					} catch (Exception e) {
						logger.error("callback invoke error ,client:" + client,e);
					}
				}else {
					try {
						callback.onException(new RpcException(response.getErrorCode(),response.getErrorDes()));
					} catch (Exception e) {
						logger.error("callback invoke error ,client:" + client,e);
					}
				}
			}else {
				logger.warn("The timeout response, response :" + response + ",client :" + client);
			}
		}finally {
			RequsetContext.remove(sequence);
		}
	}
	
	private String getTimeoutInfo(long curTimestamp) {
        return "Server-side response timeout Sending request timeout in client-side start time: " 
                    + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(requestTimestamp))) + ", end time: " 
                    + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date())) + ", server elapsed:" + (curTimestamp - requestTimestamp)
                    + ",request:" + request + ",client :" + client;
    }
	
	private static class RequestTimeoutScan implements Runnable {
        public void run() {
            while (true) {
                try {
                	for (RequsetContext content : CONTEXT_CACHE.values()) {
                	  long curTime = System.currentTimeMillis();
                      if (curTime - content.getRequestTimestamp() > content.getTimeout()) {
                    	  NetRequest request = content.getRequest();
                    	  NetResponse timeoutResponse = NetResponse.build(request);
                    	  timeoutResponse.setErrorCode(ErrorCode.SERVER_TIMEOUT.getValue());
                    	  timeoutResponse.setErrorDes(content.getTimeoutInfo(curTime));
                    	  RequsetContext.handleReponse(content.getClient(), timeoutResponse);
                      }
                	}
                    Thread.sleep(50);
                } catch (Throwable e) {
                    logger.error("Exception when scan the timeout request.", e);
                }
            }
        }
    }
	
    static {
        Thread th = new Thread(new RequestTimeoutScan(), "ResponseTimeoutScanTimer");
        th.setDaemon(true);
        th.start();
    }
}
