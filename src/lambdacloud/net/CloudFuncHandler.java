package lambdacloud.net;

import static symjava.symbolic.Symbol.x;
import static symjava.symbolic.Symbol.y;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import symjava.symbolic.Expr;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudVar;

/**
 * Handler for a client-side channel.  This handler maintains stateful
 * information which is specific to a certain channel using member variables.
 * Therefore, an instance of this handler can cover only one channel.  You have
 * to create a new handler instance whenever you create a new channel and insert
 * this handler to avoid a race condition.
 */
public class CloudFuncHandler extends SimpleChannelInboundHandler<CloudFuncResp> {

    final BlockingQueue<CloudFuncResp> answer = new LinkedBlockingQueue<CloudFuncResp>();
    public ChannelHandlerContext ctx;
    public CloudFuncResp getCloudResp() {
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
//        System.out.println("CloudFuncHandler.channelActive:");
////		CloudFunc func = new CloudFunc("func123", new Expr[]{x,y}, x*y);
////        //this.send(func);
//		System.out.println(Thread.currentThread().getName());
//    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, final CloudFuncResp msg) {
    	System.out.println("CloudFuncResp messageReceived:");
    	answer.offer(msg);
//    	System.out.println(msg);
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

    public boolean send(CloudFunc func) {
        ChannelFuture future = null;
        future = ctx.writeAndFlush(func);
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
