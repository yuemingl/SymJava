package lambdacloud.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import lambdacloud.core.CSD;

public class CloudVarHandler extends SimpleChannelInboundHandler<CSD> {

	final BlockingQueue<CSD> queue = new LinkedBlockingQueue<CSD>();

	public CSD getCloudVar() {
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
	public void messageReceived(ChannelHandlerContext ctx, final CSD msg) {
		System.err.println("messageReceived: CloudVar");
		queue.offer(msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
