package org.hdl.hpgsc.remoting;

import java.net.InetSocketAddress;

import org.hdl.hpgsc.common.Configuration;

/**
 * Endpoint
 * @author qiuhd
 * @since  2014-7-24
 * @version V1.0.0
 */
public interface Endpoint {
	/**
	 * Return {@linkplain AppConf}
	 * @return
	 */
	Configuration getConf();
	/**
	 * Return local address
	 * @return
	 */
	InetSocketAddress getLocalAddress();
	/**
	 * Return channel handler
	 * @return
	 */
	ChannelHandler getChannelHandler();
	/**
	 * Send message to this channel
	 * @param message
	 * @throws RemotingException
	 */
	void send(Object message) throws RemotingException;
	/**
     * send message.
     * 
     * @param message
     * @param sent 是否已发送完成
     */
    void send(Object message, boolean sent) throws RemotingException;
	/**
	 * Close this channel
	 */
	void close();
	/**
	 * Close delay
	 * @param timeout
	 */
	void close(int timeout);
	/**
	 * Is closed
	 * @return
	 */
	boolean isClosed();
}

