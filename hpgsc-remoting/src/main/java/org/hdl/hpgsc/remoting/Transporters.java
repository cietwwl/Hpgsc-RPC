package org.hdl.hpgsc.remoting;

import org.hdl.hpgsc.common.Configuration;
import org.hdl.hpgsc.remoting.support.AbstractClient;
import org.hdl.hpgsc.remoting.support.AbstractServer;
import org.hdl.hpgsc.remoting.support.NettyTransporter;

/**
 * Transporters
 * @author qiuhd
 * @since  2014年8月13日
 */
public final class Transporters {

	private static Transporter transporter = new NettyTransporter();
	
	public static AbstractServer bind(Configuration conf,ChannelHandler handler,Codec codec) throws RemotingException{
		return transporter.bind(conf, handler,codec);
	}
	
	public static AbstractClient connect(Configuration conf,ChannelHandler handler, Codec codec) throws RemotingException {
		return transporter.connect(conf, handler,codec);
	}
}

