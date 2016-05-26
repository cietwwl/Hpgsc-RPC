package org.hdl.hpgsc.remoting;


/**
 * Client
 * @since  2014年8月12日
 */
public interface Client extends Endpoint, Channel{
	/**
	 * Reconnect 
	 * @throws RemotingException
	 */
	void reconnect() throws RemotingException;
	/**
	 * Return connect timeout 
	 * @return
	 */
	int getConnectTimeout();
}

