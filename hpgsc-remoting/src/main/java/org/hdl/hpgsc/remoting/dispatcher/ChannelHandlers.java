package org.hdl.hpgsc.remoting.dispatcher;

import org.hdl.hpgsc.common.Configuration;
import org.hdl.hpgsc.remoting.ChannelHandler;
import org.hdl.hpgsc.remoting.support.MultiMessageHandler;

/**
 * 
 * @author qiuhd
 * @since  2014年9月3日
 */
public class ChannelHandlers {

	public static ChannelHandler wrap(ChannelHandler handler, Configuration conf){
        return ChannelHandlers.getInstance().wrapInternal(handler, conf);
    }

    protected ChannelHandlers() {}

    protected ChannelHandler wrapInternal(ChannelHandler handler, Configuration conf) {
        return new MultiMessageHandler(ExecutionDispather.getInstance().dispatch(handler, conf));
    }

    private static ChannelHandlers INSTANCE = new ChannelHandlers();

    protected static ChannelHandlers getInstance() {
        return INSTANCE;
    }

    static void setTestingChannelHandlers(ChannelHandlers instance) {
        INSTANCE = instance;
    }
}

