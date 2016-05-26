package org.hdl.hggsc.rpc.client.event;

import java.util.HashMap;
import java.util.Map;

import org.hdl.hggsc.rpc.client.IClient;
import org.hdl.hggsc.rpc.protocol.NetEvent;
import org.hdl.hpgsc.common.io.Record;
import org.hdl.hpgsc.common.utils.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultEventDispatcher implements EventDispatcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultEventDispatcher.class);
	
	private final IClient client;
	
	private Map<Long, ServerEventListener> listeners = new HashMap<Long, ServerEventListener>();
	
	private Map<Long, Class<? extends Record>> parames = new HashMap<Long, Class<? extends Record>>();
	
	public DefaultEventDispatcher(IClient client) {
		this.client = client;
	}
	
	@Override
	public void addListener(long id, ServerEventListener listener,Class<? extends Record> parame) {
		Preconditions.checkArgument(listener != null, "listener must not be null!!");
		listeners.put(id, listener);
		parames.put(id, parame);
	}
	
	@Override
	public void dispatch(NetEvent event) {
		long id = event.getId();
		ServerEventListener listener = listeners.get(id);
		if (listener != null) {
			try {
				listener.handleEvent(event);
			} catch (Throwable t) {
				LOGGER.error("Failed to handle event,event :" + event.getId() + ",client :" + client);
			}
		}else {
			throw new IllegalStateException("Unfound event listener,event id:" + event.getId() + ",client :" + client);
		}
	}

	@Override
	public Class<? extends Record> getParame(long id) {
		return parames.get(id);
	}
}
