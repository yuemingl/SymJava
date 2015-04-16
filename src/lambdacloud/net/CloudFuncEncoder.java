package lambdacloud.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import symjava.bytecode.IR;
import lambdacloud.core.CloudFunc;

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
		out.writeInt(funcIR.type);
		out.writeInt(nameLen);
		out.writeInt(dataLen);
		out.writeBytes(allData);
	}
}
