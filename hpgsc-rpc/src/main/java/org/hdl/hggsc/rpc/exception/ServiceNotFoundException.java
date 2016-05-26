package org.hdl.hggsc.rpc.exception;

/**
 * ServiceNotFoundException : 服务端未找到服务异常
 * @author qiuhd
 */
public class ServiceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 5196421433506179782L;

    /**
     * Creates a new instance of ServiceNotFoundException.
     */
    public ServiceNotFoundException() {
        super();
    }

    /**
     * Creates a new instance of ServiceNotFoundException.
     * 
     * @param message
     * @param t
     */
    public ServiceNotFoundException(String message, Throwable t) {
        super(message, t);
    }

    /**
     * Creates a new instance of ServiceNotFoundException.
     * 
     * @param message
     */
    public ServiceNotFoundException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of ServiceNotFoundException.
     * 
     * @param t
     */
    public ServiceNotFoundException(Throwable t) {
        super(t);
    }

}
