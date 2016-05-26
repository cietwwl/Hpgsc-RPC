package org.hdl.hggsc.rpc.protocol;

import org.hdl.hpgsc.common.io.Bytes;

/**
 * @author qiuhd
 *
 *  <pre>
 *       Byte/     0       |       1       |       2       |       3       |
 *          /              |               |               |               |
 *         |0 1 2 3 4 5 6 7|0 1 2 3 4 5 6 7|0 1 2 3 4 5 6 7|0 1 2 3 4 5 6 7|
 *         +---------------+---------------+---------------+---------------+
 *        0| Magic                         |  Type         | ServiceId     |
 *         +---------------+---------------+---------------+               +
 *        4|                                                               |
 *         +											   +---------------+
 *        8|                                               |     length    |
 *         +---------------+---------------+---------------+---------------+ 
 *       12|                                               |               |
 *         +---------------+---------------+---------------+---------------|                                                                          
 *         Total 15 bytes
 *  </pre>
 */
public class NetHeader {
	/**
	 * 消息头长度
	 */
	public static final int HEADER_LENGTH = 15;
	/**
	 * 
	 */
	public static final short MAGIC_CODE = (short) 0xdabb;
	
	public static final byte MAGIC_HIGH = Bytes.short2bytes(MAGIC_CODE)[0];

	public static final byte MAGIC_LOW = Bytes.short2bytes(MAGIC_CODE)[1];
	/**
	 * 消息体的长度在头中位置
	 */
	public static final byte LENGTH_POSITION = 11;
	/**
	 * 消息类型 {@link NetMessage#mType}
	 */
	private byte mType;
	/**
	 * 服务Id
	 */
	private long id;
	/**
	 * 消息体长度
	 */
	private int length;
	
	public NetHeader() {
		
	}
	
	public NetHeader(byte type,long id) {
		this.mType = type;
		this.id = id;
	}
	
	public byte getType() {
		return mType;
	}
	
	public void setType(byte type) {
		this.mType = type;
	}
	
	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}

    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/**
     * 从字节流还原NetHead头
     * @param input
     */
    public static NetHeader wrap(byte[] input) {
    	NetHeader header = new NetHeader();
    	header.mType = input[2];
    	header.id = Bytes.bytes2long(input, 3);
    	header.length = Bytes.bytes2short(input, LENGTH_POSITION);
    	return header;
    }
}
