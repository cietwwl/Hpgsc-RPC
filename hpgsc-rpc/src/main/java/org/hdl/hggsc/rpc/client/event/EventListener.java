package org.hdl.hggsc.rpc.client.event;

import org.hdl.hggsc.rpc.protocol.NetEvent;
/**
 * EventListener
 * @author qiuhd
 *
 * @param <T>
 */
public interface EventListener {

	 void handleEvent(NetEvent event);
	
}
