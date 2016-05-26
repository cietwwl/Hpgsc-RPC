package org.hdl.hggsc.rpc.client.event;

import org.hdl.hggsc.rpc.protocol.NetEvent;
import org.hdl.hpgsc.common.io.Record;
/**
 * EventDispatcher
 * @author qiuhd
 *
 */
public interface EventDispatcher {
	
	void addListener(long id, EventListener listener,Class<? extends Record> parame);
	
	Class<? extends Record> getParame(long id);
	
	void dispatch(NetEvent event);
	
}
