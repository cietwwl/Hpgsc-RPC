package org.hdl.hggsc.rpc.protocol;

/**
 * Response message.
 * @author qiuhd
 * @since  2014年9月11日
 * @version V1.0.0
 */
public class NetResponse extends NetMessage{
    /**
     * 常用的系统的错误码
     * @author qiuhd
     *
     */
    public enum ErrorCode {
    	OK(20,""),
    	SERVICE_NOT_FOUND(30, "Service not found"),
    	BAD_REQUEST(40,"Request format error"),
    	BAD_RESPONSE(50,"Response format error"),
    	SERVICE_ERROR(60,"Invoke service error"),
    	CLIENT_ERROR(70,"Client error"),
    	SERVER_TIMEOUT(80,"Client request timeout");

    	private int value;
    	
    	private String desc;
    	
    	private ErrorCode(int value,String desc) {
    		this.value = value;
    		this.desc = desc;
    	}

		public int getValue() {
			return value;
		}
		
		public String getDesc() {
			return desc;
		}
    }
    
	private int errorCode = ErrorCode.OK.getValue();
	
	private String errorDes;
	/**
	 * 客户端随机生成的序列号，服务器转发该序列
	 */
	private long sequence;
	
	/**
	 * 对应的请求
	 */
	private NetRequest request;
	/**
	 * 静态方法
	 * @param request
	 * @return
	 */
	public static NetResponse build(NetRequest request) {
		NetResponse response = new NetResponse(request.getId());
		response.setSequence(request.getSequence());
		response.request = request;
		return response;
	}
	/**
	 * 静态方法
	 * @param request
	 * @return
	 */
	public static NetResponse buildRegsiterResponse(NetRequest request) {
		NetResponse response = new NetResponse(request.getId(),MSG_TYPE_REGISTER_RESP);
		response.setSequence(request.getSequence());
		response.request = request;
		return response;
	}
	/**
	 * 静态方法
	 * @param request
	 * @return
	 */
	public static NetResponse build(NetRequest request,ErrorCode errorCode) {
		NetResponse response = build(request);
		if (errorCode != null) {
			response.errorCode = errorCode.getValue();
			response.errorDes = errorCode.getDesc();
		}
		return response;
	}
	
	public NetResponse(long id) {
		super(id, MSG_TYPE_RESPONSE);
	}
	
	public NetResponse(long id,byte type) {
		super(id, type);
	}
	
	public NetResponse(long id,int errorCode) {
		this(id);
		this.errorCode = errorCode;
	}
	
	public NetResponse(long id,int errorCode,String errorDes) {
		this(id,errorCode);
		this.errorDes = errorDes;
	}
	
	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDes() {
		return errorDes;
	}

	public void setErrorDes(String errorDes) {
		this.errorDes = errorDes;
	}

	public long getSequence() {
		return sequence;
	}

	public void setSequence(long sequence) {
		this.sequence = sequence;
	}

	public boolean isOK() {
		return this.errorCode == ErrorCode.OK.getValue() ? true : false;
	}
	
	public NetRequest getRequest() {
		return request;
	}
	
	public void setRequst(NetRequest request) {
		this.request = request;
	}
	
	public boolean isRegister() {
		return this.mType == MSG_TYPE_REGISTER_RESP ? true : false; 
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[NetResponse : {");
		builder.append(" Type :").append(typeToString()).append(",");
		builder.append(" ServiceId :").append(getId()).append(",");
		builder.append(" ErrorCode :").append(errorCode).append(",");
		builder.append(" ErrorMsg :").append(errorDes);
		builder.append("}]");
		return builder.toString();
	}
}
