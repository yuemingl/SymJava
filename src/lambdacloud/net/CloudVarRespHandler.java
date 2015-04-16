package lambdacloud.net;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import lambdacloud.core.CloudVar;

/**
 * Handler for a client-side channel.  This handler maintains stateful
 * information which is specific to a certain channel using member variables.
 * Therefore, an instance of this handler can cover only one channel.  You have
 * to create a new handler instance whenever you create a new channel and insert
 * this handler to avoid a race condition.
 */
public class CloudVarRespHandler extends SimpleChannelInboundHandler<CloudVarResp> {
    final BlockingQueue<CloudVarResp> answer = new LinkedBlockingQueue<CloudVarResp>();
    public ChannelHandlerContext ctx;
    public CloudVarResp getCloudResp() {
        boolean interrupted = false;
        try {
            for (;;) {
                try {
                    return answer.take();
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

//    @Override
//    public void channelActive(ChannelHandlerContext ctx) {
//        this.ctx = ctx;
//        System.out.println("channelActive:");
////        try {
////			ctx.writeAndFlush("Hello!").sync();
////		} catch (InterruptedException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
////        CloudVar var = new CloudVar("var0").init(1,2,3);
////        this.send(var);
//        //sendNumbers();
//    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, final CloudVarResp msg) {
    	System.out.println("CloudVarResp messageReceived");
    	answer.offer(msg);
//        ctx.channel().close().addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture future) {
//                boolean offered = answer.offer(msg);
//                assert offered;
//            }
//        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public boolean send(CloudVar var) {
        // Do not send more than 4096 numbers.
        ChannelFuture future = null;
        future = ctx.write(var);
        ctx.flush();
        try {
			future.sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (future.isSuccess()) {
           return true;
        } else {
            future.cause().printStackTrace();
            future.channel().close();
        }        
       //future.addListener(numberSender);
		return false;
   }

//    private final ChannelFutureListener numberSender = new ChannelFutureListener() {
//        @Override
//        public void operationComplete(ChannelFuture future) throws Exception {
//            if (future.isSuccess()) {
//                sendNumbers();
//            } else {
//                future.cause().printStackTrace();
//                future.channel().close();
//            }
//        }
//    };
}
