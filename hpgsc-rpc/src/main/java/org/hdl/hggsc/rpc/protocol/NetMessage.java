package org.hdl.hggsc.rpc.protocol;
/**
 * 
 * @author qiuhd
 * @since  2014年9月12日
 * @version V1.0.0
 */
public abstract class NetMessage {
	/**
	 * Request type
	 */
	public static final byte MSG_TYPE_REQUEST = 0x02;
	/**
	 * Response type
	 */
	public static final byte MSG_TYPE_RESPONSE = 0x04;
	/**
	 * Push type
	 */
	public static final byte MSG_TYPE_PUSH = 0x10;
	/**
	 * Register request
	 */
	public static final byte MSG_TYPE_REGISTER_REQ = 0x20;
	/**
	 * Register response
	 */
	public static final byte MSG_TYPE_REGISTER_RESP = 0x40;
	/**
	 * HEARTBEAT TYPE
	 */
	public static final byte MSG_TYPE_HEARTBEAT = 0x01;
	
	/**
	 * id
	 */
	protected final long id;
	/**
	 * 消息类型
	 */
	protected final byte mType;
	/**
	 * 客户端类型
	 */
	protected String clientType;
	/**
	 * 消息内容长度
	 */
	private int length = 0;
	/**
	 * 消息内容
	 */
	private Object content;
	
	public NetMessage(long id,byte type) {
		this.id = id;
		this.mType = type;
	}
	
	public Object getContent() {
		return content;
	}

	public void setContent(Object object) {
		this.content = object;
	}

	public long getId() {
		return id;
	}

	public byte getType() {
		return mType;
	}
	
	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String typeToString() {
		if (mType == MSG_TYPE_REQUEST) {
			return "request";
		}else if (mType == MSG_TYPE_RESPONSE) {
			return "response";
		}else if (mType == MSG_TYPE_PUSH) {
			return "push";
		}
		return "N/A" ;
	}
}
