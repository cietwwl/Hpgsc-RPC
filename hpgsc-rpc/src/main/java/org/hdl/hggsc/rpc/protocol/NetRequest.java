package org.hdl.hggsc.rpc.protocol;

import org.hdl.hggsc.rpc.client.ResponseCallback;
import org.hdl.hpgsc.common.io.Record;



/**
 * 
 * Request message.
 * @author qiuhd
 * @since  2015年4月19日
 * @version V1.0.0
 */
public class NetRequest extends NetMessage {
	
	/**
	 * 客户端随机生成的序列号，服务器转发该序列
	 */
	private long sequence;
	/**
	 * 双向通讯标识
	 * 如果{@true} 表示 client <--> server
	 * 如果{false} 表示 client  --> server
	 */
	private boolean twoWay;
	/**
	 * 消息解析出现错误
	 */
	private boolean mBroken = false;
	
	private ResponseCallback<? extends Record> callback;
	
	public NetRequest(long serviceId) {
		super(serviceId, MSG_TYPE_REQUEST);
	}
	
	public NetRequest(long serviceId,byte type) {
		super(serviceId, type);
	}
	
	public static NetRequest buildRegisterReq(RegisterReq req) {
		NetRequest request = new NetRequest(0,NetMessage.MSG_TYPE_REGISTER_REQ);
		request.setTwoWay(true);
		request.setContent(req);
		return request;
	}
	
	public long getSequence() {
		return sequence;
	}

	public void setSequence(long sequence) {
		this.sequence = sequence;
	}
	
	public boolean isBroken() {
		return mBroken;
	}

	public void setBroken(boolean broken) {
		this.mBroken = broken;
	}

	public boolean isTwoWay() {
		return twoWay;
	}

	public void setTwoWay(boolean twoWay) {
		this.twoWay = twoWay;
	}
	
	public boolean isRegister() {
		return this.mType == MSG_TYPE_REGISTER_REQ ? true : false; 
	}

	public ResponseCallback<? extends Record> getCallback() {
		return callback;
	}

	public void setCallback(ResponseCallback<? extends Record> callback) {
		this.callback = callback;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[NetRequest : {");
		builder.append(" Type :").append(typeToString()).append(",");
		builder.append(" ServiceId :").append(getId()).append(",");
		builder.append(" Sequence :").append(sequence).append(",");
		builder.append(" twoWay :").append(twoWay).append(",");
		builder.append(" mBroken :").append(mBroken).append(",");
		builder.append(" Length :").append(getLength());
		builder.append("}]");
		return builder.toString();
	}
}
