package org.hdl.hpgsc.remoting;

import org.hdl.hpgsc.common.Configuration;
import org.hdl.hpgsc.remoting.support.AbstractClient;
import org.hdl.hpgsc.remoting.support.AbstractServer;

/**
 * Transporter
 * @author qiuhd
 * @since  2014-8-1
 * @version V1.0.0
 */
public interface Transporter {
	
	/**
	 * Start server
	 * @param conf
	 * @param handler
	 * @param codec
	 * @return
	 * @throws RemotingException
	 */
	AbstractServer bind(Configuration conf,ChannelHandler handler,Codec codec) throws RemotingException ;
	/**
	 * Connect remote server
	 * @param conf
	 * @param handler
	 * @param codec
	 * @return
	 * @throws RemotingException
	 */
	AbstractClient connect(Configuration conf,ChannelHandler handler, Codec codec) throws RemotingException ; 
}

