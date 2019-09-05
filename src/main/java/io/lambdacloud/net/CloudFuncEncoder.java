package io.lambdacloud.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import io.lambdacloud.symjava.bytecode.IR;
import io.lambdacloud.core.CloudFunc;
import io.lambdacloud.core.CloudFunc.FUNC_TYPE;

/**
 */
public class CloudFuncEncoder extends MessageToByteEncoder<CloudFunc> {

	@Override
	protected void encode(ChannelHandlerContext ctx, CloudFunc func, ByteBuf out) {
		// Convert to a BigInteger first for easier implementation.
		IR funcIR = func.getFuncIR();
		
		int nameLen = 0;
		int dataLen = funcIR.bytes.length;
		int packageLen = 0;
		byte[] nameBytes = null;
		byte[] allData = null;
		try {
			nameBytes = funcIR.name.getBytes("UTF-8");
			nameLen = nameBytes.length;
			packageLen = nameLen + dataLen;
			allData = new byte[packageLen];
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		ByteBuffer buf = ByteBuffer.wrap(allData);
		buf.put(nameBytes);
		buf.put(funcIR.bytes);

		// Write a message.
		out.writeByte((byte) 'F'); // magic number
		out.writeInt(getFuncType(funcIR.type));
		out.writeInt(func.getOutAryLen());
		out.writeInt(func.getNumArgs());
		out.writeInt(nameLen);
		out.writeInt(dataLen);
		out.writeBytes(allData);
	}
	
	protected int getFuncType(FUNC_TYPE funcType) {
		switch(funcType) {
		case SCALAR:
			return 1;
		case VECTOR:
			return 2;
		case BATCH:
			return 3;
		}
		return -1;
	}
}
