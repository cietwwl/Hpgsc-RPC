package org.hdl.hggsc.rpc.service;

import javax.management.ServiceNotFoundException;

import org.hdl.hggsc.rpc.channel.ExchangeChannel;
import org.hdl.hggsc.rpc.protocol.NetRequest;
import org.hdl.hggsc.rpc.protocol.NetResponse;
import org.hdl.hggsc.rpc.protocol.NetResponse.ErrorCode;
import org.hdl.hggsc.rpc.server.HpgscServer;
import org.hdl.hpgsc.common.io.Record;
import org.hdl.hpgsc.common.utils.StringUtils;
import org.hdl.hpgsc.remoting.Channel;
import org.hdl.hpgsc.remoting.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RequestDispatcher
 * @author qiuhd
 *
 */
public class RequestHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);
	ServiceLocator serviceLocator;
	ServiceHandleAdapter serviceHandleAdapter = new MethodHandlerAapater();
	private final HpgscServer server;
	
	public RequestHandler(HpgscServer server) {
		this.server = server;
		serviceLocator = server.getServiceLocator();
	}
	
//	public void dispatch(Channel channel,NetRequest request) throws RemotingException {
//		if (request.isBroken()) {
//			handleBadRequest(request,channel);
//			return ;
//		}
//		
//		ServiceExecutionChain handlerChain = serviceLocator.getHandler(request);
//		if (handlerChain == null) {
//			handleUnknownRequest(request,channel);
//			return ;
//		}
//		
//		NetResponse response = wrapResponse(request);
//		Object result = null;
//		try {
//			ExchangeChannel exchangeChannel = ExchangeChannel.getOrAddChannel(channel,server);
//			try {
//				if(!handlerChain.applyPreHandle(request, exchangeChannel)) {
//					if (response != null) {
//						if (response.getErrorCode() == ErrorCode.OK.getValue()) {
//							response.setErrorCode(ErrorCode.SERVICE_ERROR.getValue());
//							response.setErrorDes(ErrorCode.SERVICE_ERROR.getDesc());
//						}
//						channel.send(response, true);
//					}
//					return;
//				}
//			}catch (Exception e) {
//				if (response != null) {
//					response.setErrorCode(ErrorCode.SERVICE_ERROR.getValue());
//					response.setErrorDes(ErrorCode.SERVICE_ERROR.getDesc());
//					channel.send(response, true);
//					return;
//				}
//				LOG.error("Failed to execute filter#onPre",e);
//			}
//			
//			try {
//				result = serviceHandleAdapter.handle(request, response,exchangeChannel, handlerChain.getHandler());
//				if (response != null) {
//					if (result != null && result instanceof Record) {
//						response.setContent(result);
//					}
//					channel.send(response, true);
//				}
//			}catch(Exception e) {
//				if (response != null) {
//					response.setErrorCode(ErrorCode.SERVICE_ERROR.getValue());
//					response.setErrorDes(ErrorCode.SERVICE_ERROR.getDesc());
//					channel.send(response, true);
//				}
//				LOG.error("Failed to invoke service,Service id :" + request.getId(),e);
//				return;
//			}
//			
//			try {
//				handlerChain.applyPostHandle(request, exchangeChannel, result);
//			}catch(Exception e) {
//				LOG.error("Failed to execute filter#onPost",e);
//			}
//		} catch (Throwable t) {
//			LOG.error("Failed to invocation target service",t);
//		}
//	}
	
	public NetResponse handleRequest(Channel channel,NetRequest request){
		if (request.isBroken()) {
			return handleBadRequest(request, channel);
		}
		
		ServiceExecutionChain executionChain = serviceLocator.getHandler(request);
		if (executionChain == null) {
			return handleUnknownRequest(request,channel);
		}
		
		ExchangeChannel exchangeChannel = ExchangeChannel.getOrAddChannel(channel,server);
		
		try {
			if(!executionChain.applyPreHandle(request, exchangeChannel)) {
				return wrapResponse(request, ErrorCode.SERVICE_ERROR);
			}
			
			Object result = serviceHandleAdapter.handle(request,exchangeChannel, executionChain.getHandler());
			
			executionChain.applyPostHandle(request, exchangeChannel, result);
			
			NetResponse response = wrapResponse(request,result);
			return response;
		} catch (Throwable t) {
			NetResponse response = wrapResponse(request,ErrorCode.SERVICE_ERROR,StringUtils.toString(t));
			return response;
		}
	}
	
	/**
	 * Wrap Response with Request and Error code
	 * @param request
	 * @return
	 */
	private NetResponse wrapResponse(NetRequest request,ErrorCode code) {
		NetResponse response = null;
		if (request.isTwoWay()) {
			response = NetResponse.build(request,code);
		}
		return response;
	}
	
	/**
	 * Wrap Response with Request and Error code
	 * @param request
	 * @param errorDes
	 * @return
	 */
	private NetResponse wrapResponse(NetRequest request,ErrorCode code,String errorDes) {
		NetResponse response = null;
		if (request.isTwoWay()) {
			response = NetResponse.build(request,code);
			response.setErrorDes(errorDes);
		}
		return response;
	}
	
	/**
	 * Wrap Response with Request and content
	 * @param request
	 * @param content
	 * @return
	 */
	private NetResponse wrapResponse(NetRequest request,Object content) {
		NetResponse response = null;
		if (request.isTwoWay()) {
			response = NetResponse.build(request);
			if (content instanceof Record) {
				response.setContent(content);
			}
		}
		return response;
	}
	
	/**
	 * Handle service not found
	 * @param request
	 * @param session
	 * @throws NetResponse 
	 */
	private NetResponse handleUnknownRequest(NetRequest request,Channel channel){
		NetResponse response = null;
		if (request.isTwoWay()) {
			response = NetResponse.build(request,ErrorCode.SERVICE_NOT_FOUND);
			response.setErrorDes("Unfound servcie id:" + request.getId());
		}
		LOG.error("Service not fount",new ServiceNotFoundException("Unfound servcie id :" + request.getId()));
		return response;
	}

	/**
	 * Handle bad request
	 * @param request
	 * @param session
	 * @throws RemotingException 
	 */
	private NetResponse handleBadRequest(NetRequest request,Channel channel){
		NetResponse response = null;
		if (request.isTwoWay()) {
			response = NetResponse.build(request,ErrorCode.BAD_REQUEST);
			String msg = null;
			Object data = request.getContent();
			if (data == null) msg = null;
			else if (data instanceof Throwable) {
				msg = StringUtils.toString((Throwable)data);
			}else {
				msg = data.toString();
			}
			response.setErrorDes("Failed to decode request due to: " + msg);
		}
		return response;
	}
//	public static class UnitTest {
//		
//		@Mock ARpcServer server;
//		
//		@Mock ServiceHandleAdapter handleAdapter;
//		
//		@Mock Channel channel;
//		
//		@Mock NetRequest request;
//		
//		@Mock ServiceLocator serviceLocator;
//		
//		@Mock ServiceExecutionChain executionChain;
//		
//		@Mock ExchangeChannel exchangeChannel;
//		
//		RequestHandler requestDispatcher;
//		
//		
//		@Before
//		public void before() {
//			MockitoAnnotations.initMocks(this);
//			doReturn(serviceLocator).when(server).getServiceLocator();
//			requestDispatcher = spy(new RequestHandler(server));
//			requestDispatcher.serviceHandleAdapter = handleAdapter;
//		}
//		
//		@Test
//		public void testDispatchWithRequestBrokenAndIsTwoWay() throws RemotingException {
//			doReturn(true).when(request).isBroken();
//			doReturn(true).when(request).isTwoWay();
//			doReturn(new RuntimeException("test")).when(request).getContent();
//			requestDispatcher.handleRequest(channel, request);
//			verify(channel).send(any(NetResponse.class), anyBoolean());
//		}
//		
//		@Test
//		public void testDispatchWithNotfoundService() throws RemotingException {
//			doReturn(false).when(request).isBroken();
//			doReturn(true).when(request).isTwoWay();
//			when(serviceLocator.getHandler(request)).thenReturn(null);
//			requestDispatcher.handleRequest(channel, request);
//			verify(channel).send(any(NetResponse.class), anyBoolean());
//		}
//		
//		@Test
//		public void testDispatchWithExecuteChainOnPreHanle() throws Exception {
//			doReturn(false).when(request).isBroken();
//			doReturn(true).when(request).isTwoWay();
//			doReturn(true).when(channel).isConnected();
//			when(serviceLocator.getHandler(request)).thenReturn(executionChain);
//			when(executionChain.applyPreHandle(any(NetRequest.class), any(ExchangeChannel.class))).thenReturn(false);
//			requestDispatcher.handleRequest(channel, request);
//			verify(channel).send(any(NetResponse.class), anyBoolean());
//			verify(handleAdapter,never()).handle(any(NetRequest.class), any(NetResponse.class), any(ExchangeChannel.class), anyObject());
//		}
//		
//		@Test
//		public void testDispatchWithExecuteChainOnPreHanleAndResponseNotEqOK() throws Exception {
//			doReturn(false).when(request).isBroken();
//			doReturn(true).when(request).isTwoWay();
//			doReturn(true).when(channel).isConnected();
//			final int errorCode = 1000;
//			doAnswer(new Answer<Void>() {
//				@Override
//				public Void answer(InvocationOnMock invocation)
//						throws Throwable {
//					NetResponse response = invocation.getArgumentAt(0, NetResponse.class);
//					Assert.assertTrue(response.getErrorCode() == errorCode);
//					return null;
//				}
//			}).when(channel).send(any(NetResponse.class), anyBoolean());
//			
//			when(serviceLocator.getHandler(request)).thenReturn(executionChain);
//			
//			//调用ServiceExecutionChain#applyPreHandle方法返回false,并且修改指定的错误信息
//			when(executionChain.applyPreHandle(any(NetRequest.class), any(ExchangeChannel.class))).then(new Answer<Boolean>() {
//				@Override
//				public Boolean answer(InvocationOnMock invocation)
//						throws Throwable {
//					NetResponse response = invocation.getArgumentAt(1, NetResponse.class);
//					//修改响应错误码
//					response.setErrorCode(errorCode);
//					return false;
//				}
//			});
//			
//			requestDispatcher.handleRequest(channel, request);
//			verify(channel).send(any(NetResponse.class), anyBoolean());
//			//验证从未执行serviceHandleAdapter#handle
//			verify(handleAdapter,never()).handle(any(NetRequest.class), any(NetResponse.class), any(ExchangeChannel.class), anyObject());
//		}
//		
//		@Test
//		public void testDispatchWithExecuteChainOnPreHanleThrowExeption() throws Exception {
//			doReturn(false).when(request).isBroken();
//			doReturn(true).when(request).isTwoWay();
//			doReturn(true).when(channel).isConnected();
//			doAnswer(new Answer<Void>() {
//				@Override
//				public Void answer(InvocationOnMock invocation)
//						throws Throwable {
//					NetResponse response = invocation.getArgumentAt(0, NetResponse.class);
//					Assert.assertTrue(response.getErrorCode() == ErrorCode.SERVICE_ERROR.getValue());
//					return null;
//				}
//			}).when(channel).send(any(NetResponse.class), anyBoolean());
//			when(serviceLocator.getHandler(request)).thenReturn(executionChain);
//			when(executionChain.applyPreHandle(any(NetRequest.class),  any(ExchangeChannel.class))).thenThrow(new RuntimeException("test"));
//			requestDispatcher.handleRequest(channel, request);
//			verify(channel).send(any(NetResponse.class), anyBoolean());
//			verify(handleAdapter,never()).handle(any(NetRequest.class), any(NetResponse.class), any(ExchangeChannel.class), anyObject());
//		}
//		
//		@Test
//		public void testDispatchExecutService() throws Exception {
//			doReturn(false).when(request).isBroken();
//			doReturn(true).when(request).isTwoWay();
//			doReturn(true).when(channel).isConnected();
//			when(serviceLocator.getHandler(request)).thenReturn(executionChain);
//			when(executionChain.applyPreHandle(any(NetRequest.class), any(ExchangeChannel.class))).thenReturn(true);
//			final Encodeable result = new Encodeable() {
//				@Override
//				public void encode(OutputArchive output) throws IOException {
//				}
//			};
//			doAnswer(new Answer<Void>() {
//				@Override
//				public Void answer(InvocationOnMock invocation)
//						throws Throwable {
//					NetResponse response = invocation.getArgumentAt(0, NetResponse.class);
//					Assert.assertNotNull(response.getContent());
//					Assert.assertEquals(response.getContent(), result);
//					Assert.assertTrue(response.getErrorCode() == ErrorCode.OK.getValue());
//					return null;
//				}
//		    }).when(channel).send(any(NetResponse.class), anyBoolean());
//			doReturn(result).when(handleAdapter).handle(any(NetRequest.class), any(NetResponse.class), any(ExchangeChannel.class), anyObject());
//			requestDispatcher.handleRequest(channel, request);
//			verify(channel).send(any(NetResponse.class), anyBoolean());
//			verify(executionChain,times(1)).applyPreHandle(any(NetRequest.class), any(ExchangeChannel.class));
//		}
//		
//		@Test
//		public void testDispatchExecutServiceThrowException() throws Exception {
//			doReturn(false).when(request).isBroken();
//			doReturn(true).when(request).isTwoWay();
//			doReturn(true).when(channel).isConnected();
//			when(serviceLocator.getHandler(request)).thenReturn(executionChain);
//			doAnswer(new Answer<Void>() {
//				@Override
//				public Void answer(InvocationOnMock invocation)
//						throws Throwable {
//					NetResponse response = invocation.getArgumentAt(0, NetResponse.class);
//					Assert.assertNull(response.getContent());
//					Assert.assertTrue(response.getErrorCode() == ErrorCode.SERVICE_ERROR.getValue());
//					return null;
//				}
//		    }).when(channel).send(any(NetResponse.class), anyBoolean());
//			when(executionChain.applyPreHandle(any(NetRequest.class), any(ExchangeChannel.class))).thenReturn(true);
//			doThrow(new RuntimeException("test service")).when(handleAdapter).handle(any(NetRequest.class), any(NetResponse.class), any(ExchangeChannel.class), anyObject());
//			requestDispatcher.handleRequest(channel, request);
//			verify(channel).send(any(NetResponse.class), anyBoolean());
//			verify(executionChain,never()).applyPreHandle(any(NetRequest.class), any(ExchangeChannel.class));
//		}
//	}
}
