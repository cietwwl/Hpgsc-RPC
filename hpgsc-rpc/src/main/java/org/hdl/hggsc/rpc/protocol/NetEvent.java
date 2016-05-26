package org.hdl.hggsc.rpc.protocol;

/**
 * Push message
 * @author qiuhd
 */
public class NetEvent extends NetMessage{
	
	private String source;
	
	public NetEvent(long id) {
		super(id, NetMessage.MSG_TYPE_PUSH);
	}
	
	public <T> T getParame(Class<T> parameClass) {
		Object content = getContent();
		if (content != null) {
			return parameClass.cast(content);
		}else {
			return null;
		}
	}
	
	public String getSource() {
		return this.source;
	}
	
	public void setSource(String source){
		this.source = source;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NetEvent [");
		builder.append("id=").append(id).append(",");
		builder.append("source=").append(source);
		builder.append("]");
		return builder.toString();
	}
}
