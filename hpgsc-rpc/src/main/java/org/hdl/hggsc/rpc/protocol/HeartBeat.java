package org.hdl.hggsc.rpc.protocol;
/**
 * 
 * @author qiuhd
 *
 */
public class HeartBeat extends NetMessage{
	/**
	 * 双向通讯标识
	 * 如果{@true} 表示 client <--> server
	 * 如果{false} 表示 client  --> server
	 */
	private boolean twoWay;
	
	public HeartBeat() {
		super(0, NetMessage.MSG_TYPE_HEARTBEAT);
	}
	
	public HeartBeat(long id, byte type) {
		super(id, type);
	}

	public boolean isTwoWay() {
		return twoWay;
	}

	public void setTwoWay(boolean twoWay) {
		this.twoWay = twoWay;
	}
}
