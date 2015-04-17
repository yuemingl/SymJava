package lambdacloud.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CloudFuncHandler extends
		SimpleChannelInboundHandler<CloudFuncResp> {
	final BlockingQueue<CloudFuncResp> queue = new LinkedBlockingQueue<CloudFuncResp>();
	public CloudFuncResp getCloudResp() {
		boolean interrupted = false;
		try {
			for (;;) {
				try {
					return queue.take();
				} catch (InterruptedException ignore) {
					interrupted = true;
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx,
			final CloudFuncResp msg) {
		System.err.println("messageReceived: CloudFuncResp");
		queue.offer(msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
