package org.hdl.hggsc.rpc.client;

import org.hdl.hggsc.rpc.client.event.ServerEventListener;
import org.hdl.hpgsc.common.io.Record;

/**
 * Interface of the Client
 * @author qiuhd
 *
 */
public interface IClient {
	/**
	 * 返回客户端配置
	 * @return
	 */
	HpgscClientConf getConf();
	/**
	 * 返回客户端的身份
	 * @return
	 */
	String getIdentify();
	/**
	 * 返回客户端索引
	 * @return
	 */
	int getIndex();
	/**
	 * 返回客户端是否可用
	 * @return
	 */
	boolean isUsable();
	/**
	 * 异步调用远程服务 
	 * @param sid				服务id
	 * @param requestParam		请求参数
	 * @param responseClass		返回参数类型
	 * @return
	 */
	<T extends Record> ResponseFuture<T> request(long serviceId,Record requestParam,Class<T> responseClass);
	/**
	 * 异步调用远程服务
	 * @param serviceId			服务id
	 * @param requestParam		请求参数
	 * @param responseClass     返回参数类型
	 * @param callback			响应回调
	 * @return
	 */
	<T extends Record> void request(long serviceId,Record requestParam,Class<T> responseClass,ResponseCallback<T> callback);
	/**
	 * 异步调用远程服务，不需要远程服务器响应
	 * @param sid				服务id
	 * @param content 			请求信息
	 * @return
	 */
	void reqeust(long serviceId,Record requestParam);
	/**
	 * 同步调用远程服务
	 * @param responseClass
	 * @param request
	 * @return
	 */
	<T extends Record> T syncRequest(long serviceId,Record requestParam,Class<T> responseClass);
	/**
	 * 注册事件监听
	 * @param evtId
	 * @param parameClass
	 * @param listener
	 */
	void registerListener(long evtId,Class<? extends Record> parameClass,ServerEventListener listener);
	/**
	 * 连接远程服务
	 */
	void connect();
	/**
	 * 关闭连接
	 */
	void close();
}
