package io.lambdacloud.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import io.lambdacloud.core.CloudSD;

public class CloudSDHandler extends SimpleChannelInboundHandler<CloudSD> {

	final BlockingQueue<CloudSD> queue = new LinkedBlockingQueue<CloudSD>();

	public CloudSD getCloudSD() {
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
	public void messageReceived(ChannelHandlerContext ctx, final CloudSD msg) {
		System.err.println("Received CloudSD: "+msg);
		queue.offer(msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
