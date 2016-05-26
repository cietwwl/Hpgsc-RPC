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
public class DoubleParam extends RecordAdapter{

	public double value;

	public DoubleParam() {}
	
	public DoubleParam(double value) {
		this.value = value;
	}
	
	@Override
	public void serialize(OutputArchive output) throws IOException {
		output.writeDouble(this.value);
	}

	@Override
	public void deserialize(InputArchive input) throws IOException {
		this.value = input.readDouble();
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
}
