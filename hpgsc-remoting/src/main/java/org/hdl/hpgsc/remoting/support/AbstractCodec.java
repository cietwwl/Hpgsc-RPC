package org.hdl.hpgsc.remoting.support;

import java.io.IOException;

import org.hdl.hpgsc.Constants;
import org.hdl.hpgsc.remoting.Channel;
import org.hdl.hpgsc.remoting.Codec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AbstractCodec
 * 
 * @author qiuhd
 */
public abstract class AbstractCodec implements Codec {
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractCodec.class);

    protected static void checkPayload(Channel channel, long size) throws IOException {
        int payload = Constants.DEFAULT_PAYLOAD;
        if (channel != null && channel.getConf() != null) {
            payload = channel.getConf().getInt(Constants.PAYLOAD_KEY, Constants.DEFAULT_PAYLOAD);
        }
        if (payload > 0 && size > payload) {
        	IOException e = new IOException("Data length too large: " + size + ", max payload: " + payload + ", channel: " + channel);
        	logger.error("checkPayLoad", e);
            throw e;
        }
    }
}