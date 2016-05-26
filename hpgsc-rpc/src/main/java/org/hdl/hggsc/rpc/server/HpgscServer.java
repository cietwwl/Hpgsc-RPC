package org.hdl.hggsc.rpc.server;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.hdl.hggsc.rpc.channel.ExchangeChannel;
import org.hdl.hggsc.rpc.channel.ExchangeChannels;
import org.hdl.hggsc.rpc.codec.CodecException;
import org.hdl.hggsc.rpc.codec.CodecFactory;
import org.hdl.hggsc.rpc.codec.RpcCodec;
import org.hdl.hggsc.rpc.handler.HeartBeatTask;
import org.hdl.hggsc.rpc.handler.HeartbeatHandler;
import org.hdl.hggsc.rpc.protocol.NetRequest;
import org.hdl.hggsc.rpc.protocol.NetResponse;
import org.hdl.hggsc.rpc.protocol.RegisterReq;
import org.hdl.hggsc.rpc.service.FilterRegistry;
import org.hdl.hggsc.rpc.service.RequestHandler;
import org.hdl.hggsc.rpc.service.ServiceEntity;
import org.hdl.hggsc.rpc.service.ServiceLocator;
import org.hdl.hpgsc.Constants;
import org.hdl.hpgsc.common.NamedThreadFactory;
import org.hdl.hpgsc.common.io.Record;
import org.hdl.hpgsc.common.utils.Preconditions;
import org.hdl.hpgsc.remoting.Channel;
import org.hdl.hpgsc.remoting.ChannelHandler;
import org.hdl.hpgsc.remoting.RemotingException;
import org.hdl.hpgsc.remoting.Transporters;
import org.hdl.hpgsc.remoting.support.AbstractChannelHandlerDelegate;
import org.hdl.hpgsc.remoting.support.AbstractServer;
import org.hdl.hpgsc.remoting.support.ChannelHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author qiuhd
 *
 */
public class HpgscServer {
	
	private FilterRegistry filterRegistry = new FilterRegistry();
	private ServiceLocator serviceLocator = new ServiceLocator(this);
	private final HpgscServerConf conf; 
	private AbstractServer server ;
	private boolean started = false;
	private RequestHandler requestDispatcher = new RequestHandler(this);
	private static final Logger LOG = LoggerFactory.getLogger(HpgscServer.class);
	private static final ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("ARpcServer-heartbeat", true));
    // 心跳定时器
    private ScheduledFuture<?> heatbeatTimer;
    // 心跳超时，毫秒。缺省0，不会执行心跳
    private int heartbeat;
    private int heartbeatTimeout;
	
	public static HpgscServer build(String identify,String bindAddress,int bindPort) {
		HpgscServerConf conf =  new HpgscServerConf();
		conf.setIdentify(identify);
		conf.set(Constants.BIND_HOST, bindAddress);
		conf.setInt(Constants.BIND_PORT, bindPort);
		HpgscServer server = new HpgscServer(conf);
		return server; 
 	}
	
	public HpgscServer(HpgscServerConf conf) {
		Preconditions.checkArgument(conf != null, "conf can not be null!!!");
		this.conf = conf;
		this.heartbeat = conf.getInt(Constants.HEARTBEAT_KEY, Constants.DEFAULT_HEARTBEAT);
		this.heartbeatTimeout = conf.getInt(Constants.HEARTBEAT_TIMEOUT_KEY, this.heartbeat * 3);
		if (heartbeatTimeout < heartbeat * 2) {
            throw new IllegalStateException("heartbeatTimeout < heartbeatInterval * 2");
        }
	}
	
	public FilterRegistry getFilterRegistry() {
		return this.filterRegistry;
	}
	
	public ServiceLocator getServiceLocator() {
		return this.serviceLocator;
	}
	
	public void registryService(Object serviceBean) {
		serviceLocator.regiserService(serviceBean);
	}
	
	public void registerFilter(Object interceptor) {
		filterRegistry.registry(interceptor);
	}
	
	public String getIdentify() {
		return this.conf.getIdentify();
	}
	
	public HpgscServerConf getConf() {
		return this.conf;
	}
	
	public synchronized void start() {
		try {
			if (started) {
				throw new IllegalStateException("Failed to start server,cause of the rpc server was already stated!!!");
			}
			RpcCodec codec = new RpcCodec(objectFactory);
			server = Transporters.bind(conf, wrapChannelHandler(requestChannelHandler),codec);
			startHeatbeatTimer();
			started = true;
		} catch (RemotingException e) {
			throw new IllegalStateException("Failed to start server,cause :" + e.getMessage(),e);
		}
	}
	
	private void startHeatbeatTimer() {
		stopHeartbeatTimer();
		if (heartbeat > 0) {
			heatbeatTimer = scheduled.scheduleWithFixedDelay(new HeartBeatTask(
					new HeartBeatTask.ChannelProvider() {
						public Collection<Channel> getChannels() {
							return Collections.unmodifiableCollection(server.getChannels());
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
        	LOG.warn(t.getMessage(), t);
        } finally {
            heatbeatTimer =null;
        }
    }
	
	private ChannelHandler wrapChannelHandler(ChannelHandler channelHandler) {
		return new HeartbeatHandler(new SystemMessageHandler(channelHandler));
	}
	
	private CodecFactory objectFactory = new CodecFactory() {
		@Override
		public Record getBySid(long serviceId) {
			ServiceEntity serviceEntity = serviceLocator.getServiceEntity(serviceId);
			if (serviceEntity != null) {
				Class<? extends Record> paramClass = serviceEntity.getParamClass();
				if (paramClass != null) {
					try {
						Record content  = paramClass.newInstance();
						return content;
					} catch (InstantiationException e) {
						throw new CodecException("Failed to create instance ( " + paramClass.getName() + " due to unfound default constructor.",e);
					} catch (IllegalAccessException e) {
						throw new CodecException("Failed to create instance ( " + paramClass.getName() + " due to " + e.getMessage(),e);
					}
				}
			}
			return null;
		}

		@Override
		public Record getBySequence(long sequence){
			throw new UnsupportedOperationException();
		}

		@Override
		public Record getByEveId(long sequence){
			throw new UnsupportedOperationException();
		}
	};
	
	public synchronized void shutdown() {
		if (!started) return ;
		if (server != null) {
			started = false;
			server.close();
			server = null;
		}
		stopHeartbeatTimer();
	}
	
	/**
	 * 接受客户端请求消息
	 */
	private final ChannelHandlerAdapter requestChannelHandler = new ChannelHandlerAdapter(){
		
		@Override
		public void caught(Channel channel, Throwable cause) throws RemotingException {
			ExchangeChannel exchangeChannel = ExchangeChannel.getOrAddChannel(channel,HpgscServer.this);
			LOG.error("Caught Exception :" + cause.getMessage() + ",in channel " + exchangeChannel);
		}

		@Override
		public void received(Channel channel, Object message) throws RemotingException {
			if (isRequest(message)) {
				NetRequest msg = (NetRequest) message;
				NetResponse response = requestDispatcher.handleRequest(channel, msg);
				if (response != null) {
					channel.send(response);
				}
			} else {
				LOG.error("Unsupported request: " + message == null ? null : message.getClass().getName() + " : " + message);
			}
		}
	};
	
	private final class SystemMessageHandler extends AbstractChannelHandlerDelegate {

		public SystemMessageHandler(ChannelHandler channelHandler) {
			super(channelHandler);
		}
		
		@Override
		public void connected(Channel channel) throws RemotingException {
			try {
				ExchangeChannel.getOrAddChannel(channel,HpgscServer.this);
			}finally {
				ExchangeChannel.removeChannelIfDisconnected(channel);
			}
			handler.connected(channel);
		}
		
		@Override
		public void disconnected(Channel channel) throws RemotingException {
			try {
				ExchangeChannel exchangeChannel = ExchangeChannel.getOrAddChannel(channel,HpgscServer.this);
				ExchangeChannels.removeChannel(exchangeChannel);
			}finally {
				ExchangeChannel.removeChannelIfDisconnected(channel);
			}
		}

		@Override
		public void received(Channel channel, Object message) throws RemotingException {
			if (isRequest(message)) {
				NetRequest request = (NetRequest) message;
				if (request.isRegister()){
					ExchangeChannel exchangeChannel = ExchangeChannel.getOrAddChannel(channel,HpgscServer.this);
					if (exchangeChannel != null) {
						RegisterReq content = (RegisterReq) request.getContent();
						exchangeChannel.setIdentify(content.getIdentify());
						exchangeChannel.setIndex(content.getIndex());
						ExchangeChannels.addChannel(exchangeChannel);
						NetResponse response = NetResponse.buildRegsiterResponse(request);
						channel.send(response);
					}
					return;
				}
			}
			handler.received(channel, message);
		}
	}
	
	private boolean isRequest(Object message) {
		return (message instanceof NetRequest) ? true:false;
	}
}
