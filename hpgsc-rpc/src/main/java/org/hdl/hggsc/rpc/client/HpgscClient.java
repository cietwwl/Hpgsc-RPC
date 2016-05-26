package org.hdl.hggsc.rpc.client;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.hdl.hggsc.rpc.client.event.DefaultEventDispatcher;
import org.hdl.hggsc.rpc.client.event.EventDispatcher;
import org.hdl.hggsc.rpc.client.event.EventListener;
import org.hdl.hggsc.rpc.codec.CodecException;
import org.hdl.hggsc.rpc.codec.CodecFactory;
import org.hdl.hggsc.rpc.codec.RpcCodec;
import org.hdl.hggsc.rpc.exception.RpcException;
import org.hdl.hggsc.rpc.handler.HeartBeatTask;
import org.hdl.hggsc.rpc.handler.HeartbeatHandler;
import org.hdl.hggsc.rpc.protocol.NetEvent;
import org.hdl.hggsc.rpc.protocol.NetRequest;
import org.hdl.hggsc.rpc.protocol.NetResponse;
import org.hdl.hggsc.rpc.protocol.NetResponse.ErrorCode;
import org.hdl.hggsc.rpc.protocol.RegisterReq;
import org.hdl.hpgsc.Constants;
import org.hdl.hpgsc.common.NamedThreadFactory;
import org.hdl.hpgsc.common.io.Record;
import org.hdl.hpgsc.common.utils.Preconditions;
import org.hdl.hpgsc.remoting.Channel;
import org.hdl.hpgsc.remoting.RemotingException;
import org.hdl.hpgsc.remoting.Transporters;
import org.hdl.hpgsc.remoting.support.AbstractClient;
import org.hdl.hpgsc.remoting.support.ChannelHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * RPC client
 * @author qiuhd
 *
 */
public class HpgscClient implements IClient {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HpgscClient.class);
	/**
	 * 客户端索引生成器
	 */
	private static final AtomicInteger INDEX_GEN = new AtomicInteger(0);
	
	private static final AtomicLong SEQUENCE_GEN = new AtomicLong(0);
	
	int index;
	
	AbstractClient client;
	
	volatile boolean registered = false;
	
    HpgscClientConf conf;
	
	ResponseFuture<Boolean> connectFuture = ResponseFuture.newInstance();
	
    EventDispatcher dispatcher = new DefaultEventDispatcher(this);
    
    private static final ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("AnimaClient-heartbeat", true));
    // 心跳定时器
    private ScheduledFuture<?> heatbeatTimer;
    // 心跳超时，毫秒。缺省0，不会执行心跳
    private int heartbeat;
    private int heartbeatTimeout;
	
	public static HpgscClient build(String identify,String remoteIp,int remotePort,int connectTimeout) {
		HpgscClientConf conf = new HpgscClientConf();
		conf.setRemoteHost(remoteIp);
		conf.setIdentify(identify);
		conf.setRemotePort(remotePort);
		conf.setConnectTimeout(connectTimeout);
		HpgscClient client = new HpgscClient(conf);
		return client;
	}
	
	public HpgscClient(HpgscClientConf conf) {
		Preconditions.checkArgument(conf != null, "conf can not be null!");
		this.conf = conf;
		this.index = INDEX_GEN.incrementAndGet();
	}
	
	@Override
	public HpgscClientConf getConf() {
		return this.conf;
	}

	@Override
	public boolean isUsable() {
		return client != null && client.isConnected() && registered;
	}

	@Override
	public String getIdentify() {
		return conf.get(Constants.IDENTIFY_KEY);
	}

	@Override
	public int getIndex() {
		return this.index;
	}
	
	@Override
	public void connect() {
		if (client != null) {
			throw new IllegalStateException("Failed to connect remote server,cause of client was already connected!!!");
		}
		try {
			RpcCodec codec = new RpcCodec(objectFactory);
			client = Transporters.connect(conf, new HeartbeatHandler(this.channelHandler), codec);
			startHeatbeatTimer();
			int conTimeout = conf.getInt(Constants.CONNECT_TIMEOUT_KEY, Constants.DEFAULT_CONNECT_TIMEOUT);
			connectFuture.await(conTimeout, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			stopHeartbeatTimer();
			throw new IllegalStateException("Failed to connect remote server,cause :" + e.getMessage(),e);
		}
	}
	
	private void startHeatbeatTimer() {
		stopHeartbeatTimer();
		if (heartbeat > 0) {
			heatbeatTimer = scheduled.scheduleWithFixedDelay(new HeartBeatTask(
					new HeartBeatTask.ChannelProvider() {
						public Collection<Channel> getChannels() {
							return Collections.<Channel> singletonList(client);
						}
					}, heartbeat, heartbeatTimeout), heartbeat, heartbeat,
					TimeUnit.MILLISECONDS);
		}
	}
	
	private void stopHeartbeatTimer() {
        try {
            ScheduledFuture<?> timer = heatbeatTimer;
            if (timer != null && ! timer.isCancelled()) {
                timer.cancel(true);
            }
        } catch (Throwable t) {
        	LOGGER.warn(t.getMessage(), t);
        } finally {
            heatbeatTimer =null;
        }
    }
	
	private CodecFactory objectFactory = new CodecFactory() {
		@Override
		public Record getBySid(long serviceId) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Record getBySequence(long sequence)  {
			RequsetContext context = RequsetContext.get(sequence);
			Class<? extends Record> responseClass = context.getResponseClass();
			if (responseClass != null) {
				try {
					Record content  = responseClass.newInstance();
					return content;
				} catch (InstantiationException e) {
					throw new CodecException("Failed to create instance ( " + responseClass.getName() + " due to " + e.getMessage(),e);
				} catch (IllegalAccessException e) {
					throw new CodecException("Failed to create instance ( " + responseClass.getName() + " due to " + e.getMessage(),e);
				}
			}
			return null;
		}
		
		@Override
		public Record getByEveId(long evtId){
			Class<? extends Record> parame = dispatcher.getParame(evtId);
			if (parame != null) {
				try {
					Record content  = parame.newInstance();
					return content;
				} catch (InstantiationException e) {
					throw new CodecException("Failed to create instance ( " + parame.getName() + " due to " + e.getMessage(),e);
				} catch (IllegalAccessException e) {
					throw new CodecException("Failed to create instance ( " + parame.getName() + " due to " + e.getMessage(),e);
				}
			}
			return null;
		}
	};
	
	private final ChannelHandlerAdapter channelHandler = new ChannelHandlerAdapter() {
		
		@Override
		public void connected(Channel channel) throws RemotingException {
			//Send register request
			channel.send(NetRequest.buildRegisterReq(new RegisterReq(getIdentify(),index)));
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Client send register request..");
			}
		}

		@Override
		public void caught(Channel channel, Throwable cause) throws RemotingException {
			LOGGER.error("Caugh Exception :" + cause.getMessage(),cause);
		}

		@Override
		public void disconnected(Channel channel) throws RemotingException {
			registered = false;
			LOGGER.warn("Client disconnected,client :" + HpgscClient.this.toString());
		}
		
		@Override
		public void received(Channel channel, Object message) throws RemotingException {
			if (message instanceof NetResponse) {
				NetResponse response = (NetResponse)message;
				if (response.isRegister()) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Client register successful!!!");
					}
					registered = true;
					connectFuture.onResponse(true);
				}else {
					RequsetContext.handleReponse(HpgscClient.this, response);
				}
			}else if (message instanceof NetEvent) {
				NetEvent event = (NetEvent) message;
				dispatcher.dispatch(event);
			}else {
				LOGGER.error("Unsupported message type :" + message.getClass().getName());
			}
		}
	};
	
	private <T extends Record> ResponseFuture<T> request(Class<T> responseClass,NetRequest request) {
		if (isUsable()) {
			try {
				long sequence = SEQUENCE_GEN.incrementAndGet();
				int timeout = conf.getInt(Constants.READ_TIMEOUT_KEY, Constants.DEFAULT_TIMEOUT);
				request.setSequence(sequence);
				ResponseFuture<T> future = ResponseFuture.newInstance();
				RequsetContext context = new RequsetContext(sequence,this,request,timeout);
				context.setCallback(future);
				context.setResponseClass(responseClass);
				client.send(request);
				return future;
			} catch (Throwable t) {
	             throw new RpcException("Failed to send request",t);
			}
		}else {
			throw new RpcException(ErrorCode.CLIENT_ERROR.getValue(),"Failed to send request,Beacause of the client disconnected!!");
		}
	}
	
	@Override
	public <T extends Record> ResponseFuture<T> request(long serviceId,Record requestParam, Class<T> responseClass) {
		NetRequest request = new NetRequest(serviceId);
		request.setTwoWay(true);
		request.setContent(requestParam);
		return request(responseClass,request);
	}
	
	@Override
	public void reqeust(long serviceId, Record content) {
		if (isUsable()) {
			try {
				long sequence = SEQUENCE_GEN.incrementAndGet();
				NetRequest reqeust = new NetRequest(serviceId);
				reqeust.setTwoWay(false);
				reqeust.setSequence(sequence);
				reqeust.setContent(content);
				client.send(reqeust);
			} catch (Throwable t) {
	             throw new RpcException("Failed to send request",t);
			}
		}else {
			throw new RpcException(ErrorCode.CLIENT_ERROR.getValue(),"Failed to send request,Beacause of the client disconnected!!");
		}
	}

	@Override
	public <T extends Record> void request(long serviceId, Record requestParam,Class<T> responseClass, ResponseCallback<T> callback) {
		if (isUsable()) {
			Preconditions.checkArgument(callback != null, "callback must be not null!!");
			try {
				NetRequest request = new NetRequest(serviceId);
				request.setTwoWay(true);
				request.setContent(requestParam);
				long sequence = SEQUENCE_GEN.incrementAndGet();
				request.setSequence(sequence);
				int timeout = conf.getInt(Constants.READ_TIMEOUT_KEY, Constants.DEFAULT_TIMEOUT);
				RequsetContext context = new RequsetContext(sequence,this,request,timeout);
				context.setCallback(callback);
				context.setResponseClass(responseClass);
				client.send(request);
			} catch (Throwable t) {
	             throw new RpcException("Failed to send request",t);
			}
		}else {
			throw new RpcException(ErrorCode.CLIENT_ERROR.getValue(),"Failed to send request,Beacause of the client disconnected!!");
		}
	}

	@Override
	public <T extends Record> T syncRequest(long serviceId,Record requestParam,Class<T> responseClass) {
		try {
			return request(serviceId,requestParam,responseClass).get();
		}catch(RpcException e) {
			throw e;
		}catch (InterruptedException e) {
			throw new RpcException(ErrorCode.CLIENT_ERROR.getValue(),"Failed to send request,Exception :" + e.getMessage(),e);
		}
	}

	@Override
	public void registerListener(long evtId, Class<? extends Record> parameClass,EventListener listener) {
		dispatcher.addListener(evtId, listener, parameClass);
	}

	@Override
	public void close() {
		if (this.client != null) {
			this.client.close();
			this.registered = false;
			this.client = null;
		}
		stopHeartbeatTimer();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AbstractRpcClient [");
		builder.append("Identify=").append(getIdentify()).append(",");
		builder.append("Index=").append(index).append(",");
		builder.append("RemoteAddress=").append(conf.getRemoteAddress().toString());
		builder.append("]");
		return builder.toString();
	}
}
