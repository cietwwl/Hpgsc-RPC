package org.hdl.hggsc.rpc.codec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.hdl.hggsc.rpc.protocol.NetHeader;
import org.hdl.hggsc.rpc.protocol.NetMessage;
import org.hdl.hpgsc.common.io.BinaryInputArchive;
import org.hdl.hpgsc.common.io.BinaryOutputArchive;
import org.hdl.hpgsc.common.io.Bytes;
import org.hdl.hpgsc.common.io.InputArchive;
import org.hdl.hpgsc.common.io.OutputArchive;
import org.hdl.hpgsc.common.io.StreamUtils;
import org.hdl.hpgsc.common.io.UnsafeByteArrayInputStream;
import org.hdl.hpgsc.common.io.UnsafeByteArrayOutputStream;
import org.hdl.hpgsc.remoting.Channel;
import org.hdl.hpgsc.remoting.support.AbstractCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * GeneralCodec
 * @author qiuhd
 */
public abstract class GeneralCodec extends AbstractCodec {

	private static final Logger logger = LoggerFactory.getLogger(GeneralCodec.class);
	
	@Override
	public Object decode(Channel channel, InputStream is) throws IOException {
		int readable = is.available();
		byte[] header = new byte[Math.min(readable, NetHeader.HEADER_LENGTH)];
		is.read(header);
		return decode(channel, is, readable, header);
	}
	
	private Object decode(Channel channel, InputStream is, int readable,
			byte[] header) throws IOException {
		// check magic code.
		if (readable > 0 && header[0] != NetHeader.MAGIC_HIGH || readable > 1
				&& header[1] != NetHeader.MAGIC_LOW) {
			int length = header.length;
			if (header.length < readable) {
				header = Bytes.copyOf(header, readable);
				// buffer.readBytes(header, length, readable - length);
				is.read(header, length, readable - length);
			}
			for (int i = 1; i < header.length - 1; i++) {
				if (header[i] == NetHeader.MAGIC_HIGH && header[i + 1] == NetHeader.MAGIC_LOW) {
					UnsafeByteArrayInputStream bis = ((UnsafeByteArrayInputStream) is);
					bis.position(bis.position() - header.length + i);
					header = Bytes.copyOf(header, i);
					break;
				}
			}
			return decode(channel, is, readable, header);
		}

		// check length.
		if (readable < NetHeader.HEADER_LENGTH) {
			return NEED_MORE_INPUT;
		}

		// get data length.
		int len = Bytes.bytes2int(header, NetHeader.LENGTH_POSITION);
		checkPayload(channel, len);

		long tt = len + NetHeader.HEADER_LENGTH;
		if (readable < tt) {
			return NEED_MORE_INPUT;
		}
		// limit input stream.
		if (readable != tt)
			is = StreamUtils.limitedInputStream(is, len);
		try {
			return decodeBody(channel, is, header);
		} finally {
			if (is.available() > 0) {
				try {
					if (logger.isWarnEnabled()) {
						logger.warn("Skip input stream " + is.available());
					}
					StreamUtils.skipUnusedStream(is);
				} catch (IOException e) {
					logger.warn(e.getMessage(), e);
				}
			}
		}
	}

	private Object decodeBody(Channel channel, InputStream is, byte[] header) throws IOException{
		NetHeader nHeader = NetHeader.wrap(header);
		InputArchive input = BinaryInputArchive.getArchive(is);
		return decodeBody(channel, is, input, nHeader);
	}

	@Override
	public void encode(Channel channel, OutputStream os, Object message)throws IOException {
		OutputArchive output = BinaryOutputArchive.getArchive(os);
		UnsafeByteArrayOutputStream ubaos = (UnsafeByteArrayOutputStream) os;
		if (message instanceof NetMessage) {
			NetMessage msg = (NetMessage)message;
			NetHeader header = new NetHeader(msg.getType(),msg.getId());
			encodePacketHeader(channel,output,header);
			encodeMessage(channel, os, output, msg);
		}else {
			throw new CodecException("Failed to encode,Unsupport message type :" + message.getClass().getName());
		}
		
		// 写入消息长度
		int msgLen = ubaos.size();
		int msgBodyLen = msgLen - NetHeader.HEADER_LENGTH;
		byte[] len = Bytes.int2bytes(msgBodyLen);
		ubaos.write(len, 0, NetHeader.LENGTH_POSITION, len.length);
	}
	
	protected void encodePacketHeader(Channel channel, OutputArchive output,NetHeader header) throws IOException {
		// write magic code
		output.writeByte(NetHeader.MAGIC_HIGH);
		output.writeByte(NetHeader.MAGIC_LOW);
		// write packet type
		output.writeByte(header.getType());
		output.writeLong(header.getId());
		// write packet length
		output.writeInt(0);
	}
	
	protected abstract Object decodeBody(Channel channel, InputStream is,InputArchive input, NetHeader pktHeader)throws IOException;
	
	protected abstract void encodeMessage(Channel channle, OutputStream os,OutputArchive output, NetMessage message) throws IOException;
}
