package org.hdl.hggsc.rpc.server;

import java.net.InetSocketAddress;

import org.hdl.hpgsc.Constants;
import org.hdl.hpgsc.common.Configuration;
import org.hdl.hpgsc.common.utils.Preconditions;
import org.hdl.hpgsc.common.utils.StringUtils;
/**
 * 服务端配置信息
 * @author qiuhd
 *
 */
public final class HpgscServerConf extends Configuration{
	
	public HpgscServerConf() {
		super();
	}

	public HpgscServerConf(HpgscServerConf other) {
		super(other);
	}
	
	public InetSocketAddress getLocalAddress() {
		String bindAddress = this.get(Constants.BIND_HOST);
		int bindPort = this.getInt(Constants.BIND_PORT,0);
		return new InetSocketAddress(bindAddress,bindPort);
	}
	
	/**
	 * 设置服务端身份
	 * @param identify
	 * @return
	 */
	public HpgscServerConf setIdentify(String identify) {
		Preconditions.checkArgument(!StringUtils.isEmpty(identify), "identify can not be empty!");
		set(Constants.IDENTIFY_KEY, identify);
		return this;
	}
	/**
	 * 返回服务身份
	 * @return
	 */
	public String getIdentify(){
		return this.get(Constants.IDENTIFY_KEY);
	}
	
	/**
	 * 设置服务端监听的主机地址
	 * @param host	
	 * @return
	 */
	public HpgscServerConf setBindHost(String host) {
		Preconditions.checkArgument(!StringUtils.isEmpty(host), "host can not be empty!");
		set(Constants.BIND_HOST, host);
		return this;
	}
	/**
	 * 设置服务端监听的端口
	 * @param port
	 * @return
	 */
	public HpgscServerConf setBindPort(int port) {
		setInt(Constants.BIND_PORT, port);
		return this;
	}
	/**
	 * 设置服务端心跳间隔时间
	 * 如果{@code ExchangeChannel} 在指定的时间内没有收到或者发送消息的时候，服务器主动发送一次心跳包给客户端，
	 * @param heartbeat
	 * @return
	 */
	public HpgscServerConf setHeartbeatInterval(int heartbeat) {
		setInt(Constants.HEARTBEAT_KEY, heartbeat);
		return this;
	}
	/**
	 * 设置服务端心跳超时时间,这个值必须大于2倍的心跳间隔时间
	 * 如果{@code ExchangeChannel} 在指定时间内没有收发消息时，服务器主动断开.
	 * @param heartbeat
	 * @return
	 */
	public HpgscServerConf setHeartbeatTimeout(int timeout) {
		setInt(Constants.HEARTBEAT_TIMEOUT_KEY, timeout);
		return this;
	}
	///////////////////////////// thread pool /////////////////////////////
	/**
	 * 设置I/O线程池的大小
	 * @param threads
	 * @return
	 */
	public HpgscServerConf setIoThreads(int threads) {
		setInt(Constants.IO_THREADS, threads);
		return this;
	}
	/**
	 * 设置业务线程池的大小
	 * @param threads
	 * @return
	 */
	public HpgscServerConf setThreads(int threads) {
		setInt(Constants.THREADS_KEY, threads);
		return this;
	}
	/**
	 * 设置业务线程池的队列大小
	 * @param threads
	 * @return
	 */
	public HpgscServerConf setThreadPoolQueue(int queue) {
		setInt(Constants.QUEUES_KEY, queue);
		return this;
	}
	/**
	 * 设置接受的字节缓存大小
	 * @param buffSize
	 * @return
	 */
	public HpgscServerConf setReceiveBuff(int buffSize) {
		setInt(Constants.SOCKET_RECEIVE_BUFFER, buffSize);
		return this;
	}
	/**
	 * 设置传输最大负载
	 * @param payload
	 * @return
	 */
	public HpgscServerConf setMaxPayload(int payload) {
		setInt(Constants.PAYLOAD_KEY, payload);
		return this;
	}
	/**
	 * 设置连接客户端数量
	 * @param clients
	 * @return
	 */
	public HpgscServerConf SetMaxClient(int clients) {
		setInt(Constants.MAX_CLIENTS, clients);
		return this;
	}
}
