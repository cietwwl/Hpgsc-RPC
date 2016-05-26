package org.hdl.hpgsc.remoting;

import org.hdl.hpgsc.common.Configuration;

/**
 * ChannelHandlerWrapper
 * 
 * @author qiudh
 */
public interface Dispather {

    /**
     * dispath.
     * 
     * @param handler
     * @param conf
     * @return channel handler
     */
    ChannelHandler dispath(ChannelHandler handler, Configuration conf);
}