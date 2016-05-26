package org.hdl.hggsc.rpc.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.hdl.hggsc.rpc.exception.RpcException;
import org.hdl.hggsc.rpc.exception.TimeoutException;
/**
 * 
 * @author qiuhd
 *
 * @param <T>
 */
public class ResponseFuture<T> implements Future<T>,ResponseCallback<T>{

    private final CountDownLatch latch = new CountDownLatch(1);
    private T result = null;
    private Throwable error = null;

    /**
     * Creates a new instance of ReponseFuture.
     */
    private ResponseFuture() {
    	
    }

    public static <T> ResponseFuture<T> newInstance() {
        return new ResponseFuture<T>();
    }
    
    /**
     * Sets the RPC response, and unblocks all threads waiting on {@link #get()} or {@link #get(long, TimeUnit)}.
     * 
     * @param result
     *            the RPC result to set.
     */
	@Override
	public void onResponse(T result) {
		this.result = result;
        latch.countDown();
	}
	
	/**
     * Sets an error thrown during RPC execution, and unblocks all threads waiting on {@link #get()} or
     * {@link #get(long, TimeUnit)}.
     * 
     * @param error
     *            the RPC error to set.
     */
	@Override
	public void onException(Throwable cause) {
		this.error = cause;
	    latch.countDown();
	}

    /**
     * Gets the value of the RPC result without blocking. Using {@link #get()} or {@link #get(long, TimeUnit)} is
     * usually preferred because these methods block until the result is available or an error occurs.
     * 
     * @return the value of the response, or null if no result was returned or the RPC has not yet completed.
     */
    public T getResult() {
        return result;
    }

    /**
     * Gets the error that was thrown during RPC execution. Does not block. Either {@link #get()} or
     * {@link #get(long, TimeUnit)} should be called first because these methods block until the RPC has completed.
     * 
     * @return the RPC error that was thrown, or null if no error has occurred or if the RPC has not yet completed.
     */
    public Throwable getError() {
        return error;
    }

    /**
     * @see java.util.concurrent.Future#get()
     */
    public T get() throws InterruptedException {
        latch.await();
        if (error != null) {
            throw new RpcException(error);
        }
        return result;
    }

    /**
     * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
     */
    public T get(long timeout, TimeUnit unit) {
        try {
            if (latch.await(timeout, unit)) {
                if (error != null) {
                    throw new RpcException(error);
                }
                return result;
            } else {
                throw new TimeoutException("CallFuture async get time out");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("CallFuture is interuptted", e);
        }
    }

    /**
     * Waits for the CallFuture to complete without returning the result.
     * 
     * @throws InterruptedException
     *             if interrupted.
     */
    public void await() throws InterruptedException {
        latch.await();
    }

    /**
     * Waits for the CallFuture to complete without returning the result.
     * 
     * @param timeout
     *            the maximum time to wait.
     * @param unit
     *            the time unit of the timeout argument.
     * @throws InterruptedException
     *             if interrupted.
     * @throws TimeoutException
     *             if the wait timed out.
     */
    public void await(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        if (!latch.await(timeout, unit)) {
            throw new TimeoutException();
        }
    }

    /**
     * @see java.util.concurrent.Future#cancel(boolean)
     */
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    /**
     * @see java.util.concurrent.Future#isCancelled()
     */
    public boolean isCancelled() {
        return false;
    }

    /**
     * @see java.util.concurrent.Future#isDone()
     */
    public boolean isDone() {
        return latch.getCount() <= 0;
    }
}
