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
public class StringParam extends RecordAdapter{

	public String value;

	public StringParam() {}
	
	public StringParam(String value) {
		this.value = value;
	}
	
	@Override
	public void serialize(OutputArchive output) throws IOException {
		output.writeString(this.value);
	}

	@Override
	public void deserialize(InputArchive input) throws IOException {
		this.value = input.readString();
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
