package org.hdl.hggsc.rpc.client;
/**
 * Interface ResponseCallback
 * @author qiuhd
 *
 */
public interface ResponseCallback<T>{

    void onResponse(T result);

    void onException(Throwable cause);
    
}
