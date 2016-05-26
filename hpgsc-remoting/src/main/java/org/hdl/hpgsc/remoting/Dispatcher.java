package org.hdl.hpgsc.remoting;

import org.hdl.hpgsc.common.Configuration;

/**
 * Dispatcher
 * @author qiudh
 */
public interface Dispatcher {

    /**
     * Dispatch
     * @param handler
     * @param conf
     * @return channel handler
     */
    ChannelHandler dispatch(ChannelHandler handler, Configuration conf);
}