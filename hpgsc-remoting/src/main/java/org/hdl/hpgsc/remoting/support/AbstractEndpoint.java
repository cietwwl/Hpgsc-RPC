package org.hdl.hpgsc.remoting.support;
import org.hdl.hpgsc.common.Configuration;
import org.hdl.hpgsc.common.utils.Preconditions;
import org.hdl.hpgsc.remoting.ChannelHandler;
import org.hdl.hpgsc.remoting.Codec;
/**
 * AbstractEndpoint
 * @author qiuhd
 * @since  2014-7-24
 * @version V1.0.0
 */
public abstract class AbstractEndpoint extends AbstractPeer{
	
	protected Codec codec ;
	
	public AbstractEndpoint(Configuration conf,ChannelHandler handler,Codec codec) {
		super(conf, handler);
		Preconditions.checkArgument(codec != null,"codec == null");
		this.codec = new MultiMessageCodec(codec);
	}

	public Codec getCodec() {
		return codec;
	}
}

