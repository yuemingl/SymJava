package lambdacloud.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.List;

import lambdacloud.core.CloudSD;

public class MessageDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) {
		// Wait until the length prefix is available.
		if (in.readableBytes() < 9) {
			return;
		}

		in.markReaderIndex();

		// Check the magic number.
		int magicNumber = in.readUnsignedByte();
		// System.err.println("Client: Magic number received: "+(char)magicNumber);
		if (magicNumber == 'V') { // CloudVar
			if (in.readableBytes() < 12) {
				in.resetReaderIndex();
				return; // Wait until the whole data is available.
			}
			int nameLen = in.readInt();
			int onCloudFlag = in.readInt();
			int dataLen = in.readInt();
			if (in.readableBytes() < nameLen + dataLen) {
				in.resetReaderIndex();
				return; // Wait until the whole data is available.
			}
			byte[] decoded = new byte[nameLen + dataLen];
			in.readBytes(decoded);
			CloudSD var = NetIOUtils.createCloudVar(decoded, nameLen, dataLen);
			var.setOnCloudFlag(onCloudFlag == 1 ? true : false);
			out.add(var);
			// System.out.println("decoded:"+var.getLabel());
		} else if (magicNumber == 'R') { // CloudResp
			if (in.readableBytes() < 16) {
				in.resetReaderIndex();
				return;
			}
			int respType = in.readInt();
			int status = in.readInt();
			int nameLen = in.readInt();
			int messageLen = in.readInt();
			if (in.readableBytes() < nameLen + messageLen) {
				in.resetReaderIndex();
				return;
			}
			byte[] decoded = new byte[nameLen + messageLen];
			in.readBytes(decoded);
			if (respType == 1)
				out.add(NetIOUtils.createCloudVarResp(decoded, status, nameLen,
						messageLen));
			else if (respType == 2)
				out.add(NetIOUtils.createCloudFuncResp(decoded, status,
						nameLen, messageLen));

		} else {
			in.resetReaderIndex();
			throw new CorruptedFrameException("Invalid magic number: "
					+ magicNumber);
		}

	}
}
