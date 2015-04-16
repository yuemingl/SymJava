package lambdacloud.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import lambdacloud.core.CloudVar;

public class CloudVarHandler extends SimpleChannelInboundHandler<CloudVar> {

	final BlockingQueue<CloudVar> queue = new LinkedBlockingQueue<CloudVar>();

	public CloudVar getCloudVar() {
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
	public void messageReceived(ChannelHandlerContext ctx, final CloudVar msg) {
		System.out.println("CloudVar messageReceived");
		queue.offer(msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
