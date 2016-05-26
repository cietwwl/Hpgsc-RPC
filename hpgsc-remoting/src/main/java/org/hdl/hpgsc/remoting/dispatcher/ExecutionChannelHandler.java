package org.hdl.hpgsc.remoting.dispatcher;

import org.hdl.hpgsc.common.Configuration;
import org.hdl.hpgsc.remoting.Channel;
import org.hdl.hpgsc.remoting.ChannelHandler;
import org.hdl.hpgsc.remoting.RemotingException;
import org.hdl.hpgsc.remoting.dispatcher.ChannelEventRunnable.ChannelState;

/**
 * Execution Channel Handler
 * @author qiuhd
 */
public class ExecutionChannelHandler extends WrappedChannelHandler {
    
    public ExecutionChannelHandler(ChannelHandler handler, Configuration conf) {
        super(handler, conf);
    }

    public void connected(Channel channel) throws RemotingException {
        executor.execute(new ChannelEventRunnable(channel, handler ,ChannelState.CONNECTED));
    }

    public void disconnected(Channel channel) throws RemotingException {
        executor.execute(new ChannelEventRunnable(channel, handler ,ChannelState.DISCONNECTED));
    }

    public void received(Channel channel, Object message) throws RemotingException {
        executor.execute(new ChannelEventRunnable(channel, handler, ChannelState.RECEIVED, message));
    }

    public void caught(Channel channel, Throwable exception) throws RemotingException {
        executor.execute(new ChannelEventRunnable(channel, handler ,ChannelState.CAUGHT, exception));
    }
}