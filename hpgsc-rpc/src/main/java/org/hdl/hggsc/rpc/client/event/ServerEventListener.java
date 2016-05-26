package org.hdl.hggsc.rpc.client.event;

import org.hdl.hggsc.rpc.protocol.NetEvent;
/**
 * EventListener
 * @author qiuhd
 *
 */
public interface ServerEventListener {

	 void handleEvent(NetEvent event);
	
}
