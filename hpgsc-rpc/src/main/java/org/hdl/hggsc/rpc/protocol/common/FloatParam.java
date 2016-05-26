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
public class FloatParam extends RecordAdapter{

	public float value;

	public FloatParam() {}
	
	public FloatParam(float value) {
		this.value = value;
	}
	
	@Override
	public void serialize(OutputArchive output) throws IOException {
		output.writeFloat(this.value);
	}

	@Override
	public void deserialize(InputArchive input) throws IOException {
		this.value = input.readFloat();
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}
}
