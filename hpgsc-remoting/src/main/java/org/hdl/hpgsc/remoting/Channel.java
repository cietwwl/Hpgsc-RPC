package org.hdl.hpgsc.remoting;

import java.net.InetSocketAddress;

import org.hdl.hpgsc.common.Configuration;

/**
 * Channel
 * @since  2014-7-24
 * @version V1.0.0
 */
public interface Channel {
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
	/**
	 * Is connected
	 * @return 
	 */
	boolean isConnected();
	/**
	 * Return remote address
	 * @return
	 */
	InetSocketAddress getRemoteAddress();
	/**
	 * Return the value of key
	 * @param key
	 * @return
	 */
	Object getAttribute(String key);
	/**
	 * Set up attribute
	 * @param key
	 * @param object
	 * @return
	 */
	void setAttribute(String key,Object object);
	/**
	 * Return true if contain key
	 * @param key
	 * @return
	 */
	boolean contains(String key);
    /**
     * Remove attribute.
     * @param key key.
     */
    void removeAttribute(String key);
}

