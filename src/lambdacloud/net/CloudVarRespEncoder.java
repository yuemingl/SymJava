package lambdacloud.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class CloudVarRespEncoder extends MessageToByteEncoder<CloudVarResp> {
	@Override
	protected void encode(ChannelHandlerContext ctx, CloudVarResp resp, ByteBuf out) {
		out.writeByte((byte) 'R'); // magic number
		byte[] bytes = resp.getBytes();
		out.writeBytes(bytes);
	}
}
