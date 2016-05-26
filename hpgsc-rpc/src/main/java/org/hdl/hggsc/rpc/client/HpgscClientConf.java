package org.hdl.hggsc.rpc.client;

import java.net.InetSocketAddress;

import org.hdl.hpgsc.Constants;
import org.hdl.hpgsc.common.Configuration;
import org.hdl.hpgsc.common.utils.Preconditions;
import org.hdl.hpgsc.common.utils.StringUtils;

/**
 * Client conf
 * 
 * @author qiuhd
 * 
 */
public class HpgscClientConf extends Configuration {

	public HpgscClientConf() {
		super();
	}

	public HpgscClientConf(HpgscClientConf other) {
		super(other);
	}

	public InetSocketAddress getRemoteAddress() {
		String remoteAddress = this.get(Constants.REMOTE_IP_KEY);
		int remotePort = this.getInt(Constants.REMOTE_PORT_KEY, 0);
		return new InetSocketAddress(remoteAddress, remotePort);
	}

	public HpgscClientConf setIdentify(String identify) {
		Preconditions.checkArgument(!StringUtils.isEmpty(identify),"identify can not be empty!");
		set(Constants.IDENTIFY_KEY, identify);
		return this;
	}

	public HpgscClientConf setRemoteHost(String remoteHost) {
		Preconditions.checkArgument(!StringUtils.isEmpty(remoteHost),"remoteAddress can not be empty!");
		set(Constants.REMOTE_IP_KEY, remoteHost);
		return this;
	}

	public HpgscClientConf setRemotePort(int remotePort) {
		setInt(Constants.REMOTE_PORT_KEY, remotePort);
		return this;
	}

	public HpgscClientConf setConnectTimeout(int timeout) {
		setInt(Constants.CONNECT_TIMEOUT_KEY, timeout);
		return this;
	}

	public HpgscClientConf setReadTimeout(int timeout) {
		setInt(Constants.READ_TIMEOUT_KEY, timeout);
		return this;
	}

	public HpgscClientConf setReconnectTime(int time) {
		setInt(Constants.RECONNECT_PERIOD_KEY, time);
		return this;
	}

	// /////////////////////////// thread pool /////////////////////////////
	/**
	 * 设置业务线程池的大小
	 * 
	 * @param threads
	 * @return
	 */
	public HpgscClientConf setThreads(int threads) {
		setInt(Constants.THREADS_KEY, threads);
		return this;
	}

	/**
	 * 设置业务线程池的队列大小
	 * 
	 * @param threads
	 * @return
	 */
	public HpgscClientConf setThreadPoolQueue(int queue) {
		setInt(Constants.QUEUES_KEY, queue);
		return this;
	}

	/**
	 * 设置接受的字节缓存大小
	 * 
	 * @param buffSize
	 * @return
	 */
	public HpgscClientConf setReceiveBuff(int buffSize) {
		setInt(Constants.SOCKET_RECEIVE_BUFFER, buffSize);
		return this;
	}
}
