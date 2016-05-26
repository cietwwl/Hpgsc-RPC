package org.hdl.hpgsc.test.benchmark;
import java.io.IOException;

import org.hdl.hpgsc.common.io.InputArchive;
import org.hdl.hpgsc.common.io.OutputArchive;
import org.hdl.hpgsc.common.io.RecordAdapter;
/**
 * Just for RPC Benchmark Test,response object
 * 
 */
public class ResponseObject extends RecordAdapter {

	private byte[] bytes = null;
	
	public ResponseObject() {
		
	}
	
	public ResponseObject(int size){
		bytes = new byte[size];
	}

	public byte[] getBytes() {
		return bytes;
	}

	@Override
	public void serialize(OutputArchive output) throws IOException {
		output.writeBuffer(this.bytes);
	}

	@Override
	public void deserialize(InputArchive input) throws IOException {
		this.bytes = input.readBuffer();
	}
}
