package org.hdl.hggsc.rpc.protocol.common;

import java.io.IOException;

import org.hdl.hpgsc.common.io.InputArchive;
import org.hdl.hpgsc.common.io.OutputArchive;
import org.hdl.hpgsc.common.io.RecordAdapter;
/**
 * 
 * @author qiuhd
 *
 */
public class LongParam extends RecordAdapter{

	public long value;

	public LongParam() {}
	
	public LongParam(long value) {
		this.value = value;
	}
	
	@Override
	public void serialize(OutputArchive output) throws IOException {
		output.writeLong(this.value);
	}

	@Override
	public void deserialize(InputArchive input) throws IOException {
		this.value = input.readLong();
	}

	public long getValue() {
		return value;
	}

	public void setValue(byte value) {
		this.value = value;
	}
}
