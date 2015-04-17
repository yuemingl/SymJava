package lambdacloud.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class CloudQueryEncoder extends MessageToByteEncoder<CloudQuery> {
	@Override
	protected void encode(ChannelHandlerContext ctx, CloudQuery req, ByteBuf out) {
		out.writeByte((byte) 'Q'); // magic number
		byte[] bytes = req.getBytes();
		out.writeBytes(bytes);
	}
}
