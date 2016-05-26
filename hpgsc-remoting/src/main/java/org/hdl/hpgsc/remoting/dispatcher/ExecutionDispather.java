package org.hdl.hpgsc.remoting.dispatcher;

import org.hdl.hpgsc.common.Configuration;
import org.hdl.hpgsc.remoting.ChannelHandler;
import org.hdl.hpgsc.remoting.Dispatcher;


/**
 * 除发送全部使用线程池处理
 * 
 * @author qiuhd
 */
public class ExecutionDispather implements Dispatcher {
    
    public static final String NAME = "execution";

    public ChannelHandler dispatch(ChannelHandler handler, Configuration conf) {
        return new ExecutionChannelHandler(handler, conf);
    }
    
    private static ExecutionDispather INSTANCE = new ExecutionDispather();

    public static ExecutionDispather getInstance() {
        return INSTANCE;
    }
}