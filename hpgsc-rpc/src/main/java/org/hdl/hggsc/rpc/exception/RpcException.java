package org.hdl.hggsc.rpc.exception;
/**
 * 
 * @author qiuhd
 *
 */
public class RpcException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8295517708169398287L;
	
	private int errorCode;

	public RpcException() {
		super();
	}
	
	public RpcException(int errorCode) {
		super();
		this.errorCode = errorCode;
	}
	
	public RpcException(int errorCode,String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public RpcException(String message, Throwable cause) {
		super(message, cause);
	}

	public RpcException(int errorCode,String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public RpcException(Throwable cause) {
		super(cause);
	}

	public int getErrorCode() {
		return errorCode;
	}
}
