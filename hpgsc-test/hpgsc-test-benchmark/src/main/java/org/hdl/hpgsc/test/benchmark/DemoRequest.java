package org.hdl.hpgsc.test.benchmark;

import java.io.IOException;

import org.hdl.hpgsc.common.io.InputArchive;
import org.hdl.hpgsc.common.io.OutputArchive;
import org.hdl.hpgsc.common.io.RecordAdapter;

public class DemoRequest extends RecordAdapter {

	private String message;

	@Override
	public void serialize(OutputArchive output) throws IOException {
		output.writeString(message);
	}

	@Override
	public void deserialize(InputArchive input) throws IOException {
		this.message = input.readString();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
