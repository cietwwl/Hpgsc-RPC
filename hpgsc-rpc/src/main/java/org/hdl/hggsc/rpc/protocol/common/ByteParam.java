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
public class ByteParam extends RecordAdapter{

	public byte value;

	public ByteParam() {}
	
	public ByteParam(byte value) {
		this.value = value;
	}
	
	@Override
	public void serialize(OutputArchive output) throws IOException {
		output.writeByte(this.value);
	}

	@Override
	public void deserialize(InputArchive input) throws IOException {
		this.value = input.readByte();
	}

	public byte getValue() {
		return value;
	}

	public void setValue(byte value) {
		this.value = value;
	}
}
