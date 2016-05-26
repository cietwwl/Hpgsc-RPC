package org.hdl.hpgsc.remoting.support;

import org.hdl.hpgsc.common.Configuration;
import org.hdl.hpgsc.remoting.ChannelHandler;
import org.hdl.hpgsc.remoting.Codec;
import org.hdl.hpgsc.remoting.RemotingException;
import org.hdl.hpgsc.remoting.Transporter;
import org.hdl.hpgsc.remoting.netty.NettyClient;
import org.hdl.hpgsc.remoting.netty.NettyServer;

/**
 * NettyTransporter
 * @author qiuhd
 * @since  2014-8-1
 * @version V1.0.0
 */
public class NettyTransporter implements Transporter{

	public AbstractServer bind(Configuration conf, ChannelHandler handler, Codec codec)
			throws RemotingException {
		return new NettyServer(conf, handler, codec);
	}

	@Override
	public AbstractClient connect(Configuration conf,ChannelHandler handler, Codec codec) throws RemotingException {
		return new NettyClient(conf, handler,codec);
	}
}

