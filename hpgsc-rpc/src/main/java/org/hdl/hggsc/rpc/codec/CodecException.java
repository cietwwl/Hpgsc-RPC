package org.hdl.hggsc.rpc.codec;
/**
 * CodecException :编解码异常
 * @author qiuhd
 *
 */
public class CodecException extends RuntimeException {

    private static final long serialVersionUID = 5196421433506179782L;

    /**
     * Creates a new instance of CodecException.
     */
    public CodecException() {
        super();
    }

    /**
     * Creates a new instance of CodecException.
     * 
     * @param message
     * @param t
     */
    public CodecException(String message, Throwable t) {
        super(message, t);
    }

    /**
     * Creates a new instance of CodecException.
     * 
     * @param message
     */
    public CodecException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of CodecException.
     * 
     * @param arg0
     */
    public CodecException(Throwable t) {
        super(t);
    }
}
