package org.hdl.hggsc.rpc.protocol;

import java.io.IOException;

import org.hdl.hpgsc.common.io.InputArchive;
import org.hdl.hpgsc.common.io.OutputArchive;
import org.hdl.hpgsc.common.io.RecordAdapter;
/**
 * 注册请求体
 * @author qiuhd
 *
 */
public class RegisterReq extends RecordAdapter {
	
	public String identify;
	public int index;
	
	public RegisterReq() {}
	
	public RegisterReq(String identify,int index) {
		this.identify = identify;
		this.index = index;
	}
	
	@Override
	public void deserialize(InputArchive input) throws IOException {
		this.identify = input.readString();
		this.index = input.readInt();
	}

	@Override
	public void serialize(OutputArchive output) throws IOException {
		output.writeString(this.identify);
		output.writeInt(index);
	}

	public String getIdentify() {
		return identify;
	}

	public int getIndex() {
		return index;
	}
}
