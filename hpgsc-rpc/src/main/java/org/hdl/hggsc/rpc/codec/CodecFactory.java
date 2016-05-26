package org.hdl.hggsc.rpc.codec;

import org.hdl.hpgsc.common.io.Record;
/**
 * 
 * @author qiuhd
 *
 */
public abstract class CodecFactory {

	public abstract Record getBySid(long serviceId);
	
	public abstract Record getBySequence(long sequence);
	
	public abstract Record getByEveId(long evtId);
}
