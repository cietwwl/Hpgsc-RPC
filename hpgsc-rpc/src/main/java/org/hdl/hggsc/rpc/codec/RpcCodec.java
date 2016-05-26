package org.hdl.hggsc.rpc.codec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.hdl.hggsc.rpc.protocol.HeartBeat;
import org.hdl.hggsc.rpc.protocol.NetEvent;
import org.hdl.hggsc.rpc.protocol.NetHeader;
import org.hdl.hggsc.rpc.protocol.NetMessage;
import org.hdl.hggsc.rpc.protocol.NetRequest;
import org.hdl.hggsc.rpc.protocol.NetResponse;
import org.hdl.hggsc.rpc.protocol.NetResponse.ErrorCode;
import org.hdl.hggsc.rpc.protocol.RegisterReq;
import org.hdl.hpgsc.common.io.InputArchive;
import org.hdl.hpgsc.common.io.OutputArchive;
import org.hdl.hpgsc.common.io.Record;
import org.hdl.hpgsc.common.utils.Preconditions;
import org.hdl.hpgsc.common.utils.StringUtils;
import org.hdl.hpgsc.remoting.Channel;
/**
 * DefaultCodec
 * @author qiuhd
 */
public class RpcCodec extends GeneralCodec {
	
	private final CodecFactory factory;
	
	public RpcCodec(CodecFactory factory) {
		Preconditions.checkArgument(factory != null, "factory must not be null!!");
		this.factory = factory;
	}
	
	@Override
	protected Object decodeBody(Channel channel, InputStream is,InputArchive input, NetHeader pktHeader) throws IOException {
		byte type = pktHeader.getType();
		if (((NetMessage.MSG_TYPE_REQUEST | NetMessage.MSG_TYPE_REGISTER_REQ) & type) != 0) {
			NetRequest request = new NetRequest(pktHeader.getId(),type);
			request.setLength(pktHeader.getLength());
			//message sequence
			long sequence = input.readLong();
			//request <-> response or request -> response
			boolean twoWay = input.readBool();
			request.setSequence(sequence);
			request.setTwoWay(twoWay);
			try {
				if (request.isRegister()) {
					decodeRegisterBody(input, request);
				}else {
					decodeRequestBody(input, request);
				}
			} catch (Throwable t) {
				request.setBroken(true);
				request.setContent(t);
			}
			return request;
		}else if (((NetMessage.MSG_TYPE_RESPONSE | NetMessage.MSG_TYPE_REGISTER_RESP) & type) != 0) {
			NetResponse response = new NetResponse(pktHeader.getId(),type);
			int errorCode = input.readInt();
			String errorDes = input.readString();
			long sequence = input.readLong();
			response.setErrorCode(errorCode);
			response.setErrorDes(errorDes);
			response.setSequence(sequence);
			if (!response.isRegister() && response.isOK()) {
				decodeResponseBody(input, response);
			}
			return response;
		}else if ((NetMessage.MSG_TYPE_PUSH & type) != 0) {
			NetEvent event = new NetEvent(pktHeader.getId());
			event.setSource(input.readString());
			Object content = decodEventBody(input,event);
			event.setContent(content);
			return event;
		}else if ((NetMessage.MSG_TYPE_HEARTBEAT & type) != 0) {
			HeartBeat heartBeat = new HeartBeat(pktHeader.getId(), type);
			boolean twoWay = input.readBool();
			heartBeat.setTwoWay(twoWay);
			return heartBeat;
		}
		return null;
	}

	@Override
	protected void encodeMessage(Channel channle, OutputStream os,OutputArchive output, NetMessage message) throws IOException {
		if (message instanceof NetResponse) {
			NetResponse response = (NetResponse) message;
			long sequence = response.getSequence();
			int errorCode = response.getErrorCode();
			String errorDes = response.getErrorDes();
			output.writeInt(errorCode);
			output.writeString(errorDes);
			output.writeLong(sequence);
			Object content = message.getContent();
			if (content != null) {
				try {
					Record record =  (Record) content;
					record.serialize(output);
				}catch(Throwable t) {
					try {
						NetResponse badResponse = new NetResponse(response.getId());
						badResponse.setSequence(response.getSequence());
						badResponse.setErrorCode(ErrorCode.BAD_RESPONSE.getValue());
						badResponse.setErrorDes("Failed to send response: " + response + ", cause: " + StringUtils.toString(t));
						channle.send(badResponse);
					} catch (Exception e) {
						throw new CodecException("Failed to send response :" + response,t);
					}
				}
			}
		}else if (message instanceof NetRequest) {
			NetRequest request = (NetRequest) message;
			output.writeLong(request.getSequence());
			output.writeBool(request.isTwoWay());
			Object content = message.getContent();
			if (content != null) {
				try {
					Record encodeable =  (Record) content;
					encodeable.serialize(output);
				}catch(Throwable t) {
					throw new CodecException("Failed to send request :" + request + " due to " + t.getMessage(),t);
				}
			}
		}else if (message instanceof NetEvent){
			NetEvent event = (NetEvent) message;
			output.writeString(event.getSource());
			Object content = message.getContent();
			if (content != null) {
				try {
					Record encodeable =  (Record) content;
					encodeable.serialize(output);
				}catch(Throwable t) {
					throw new CodecException("Failed to encode event :" + event,t);
				}
			}
		}else if (message instanceof HeartBeat) {
			HeartBeat heartBeat = (HeartBeat) message;
			output.writeBool(heartBeat.isTwoWay());
		}
	}
	
	protected void decodeResponseBody(InputArchive input,NetResponse response) {
		Record content = null;
		try {
			content = factory.getBySequence(response.getSequence());
		} catch (Throwable t) {
			response.setErrorCode(ErrorCode.CLIENT_ERROR.getValue());
			response.setContent(t);
		}
		// 序列化响应体
		if (content != null) {
			try {
				content.deserialize(input);
				response.setContent(content);
			}catch(Throwable t) {
				response.setErrorCode(ErrorCode.CLIENT_ERROR.getValue());
				response.setContent(new CodecException("Failed to decode response ( " + content.getClass().getName() + " )" ,t));
			}
		}
	}
	
	protected Object decodEventBody(InputArchive input,NetEvent event) {
		Record content = factory.getByEveId(event.getId());
		// 序列化响应体
		if (content != null) {
			try {
				content.deserialize(input);
				event.setContent(content);
			}catch(Throwable t) {
				throw new CodecException("Failed to decode event,Event id:" + event.getId() ,t);
			}
		}
		return content;
	}
	
	protected void decodeRequestBody(InputArchive input,NetRequest request) {
		Record content = null;
		try {
			content = factory.getBySid(request.getId());
		} catch (Throwable t) {
			request.setBroken(true);
			request.setContent(t);
			return ;
		}
		// 序列化请求体
		if (content != null) {
			try {
				content.deserialize(input);
				request.setContent(content);
			}catch(Throwable t) {
				request.setBroken(true);
				request.setContent(new CodecException("Failed to decode reqeust ( " + content.getClass().getName() + " ) ",t));
			}
		}
	}
	
	protected void decodeRegisterBody(InputArchive input,NetRequest request) throws IOException {
		Record content = new RegisterReq();
		content.deserialize(input);
		request.setContent(content);
	}
}
