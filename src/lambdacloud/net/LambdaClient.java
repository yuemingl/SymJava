package lambdacloud.net;


import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudVar;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

/**
 * Sends a sequence of integers to a {@link FactorialServer} to calculate
 * the factorial of the specified integer.
 */
public final class LambdaClient {

    static final boolean SSL = System.getProperty("ssl") != null;
    
    ChannelFuture f;
    CloudVarHandler varHandler;
    CloudVarRespHandler varRespHandler;
    CloudFuncHandler funcHandler;
    
    Channel ch;
    EventLoopGroup group;
    
    public void connect() throws Exception {
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
        } else {
            sslCtx = null;
        }

        group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new LambdaClientInitializer(sslCtx));

            // Make a new connection.
            f = b.connect(CloudConfig.getHost(), CloudConfig.getPort()).sync();
            ch = f.channel();
            varHandler = (CloudVarHandler)f.channel().pipeline().get("CloudVarHandler");
            varRespHandler = (CloudVarRespHandler)f.channel().pipeline().get("CloudVarRespHandler");
            funcHandler = (CloudFuncHandler)f.channel().pipeline().get("CloudFuncHandler");
            
            //ch.writeAndFlush(new CloudVar("xxxxxxx").init(1,2,3,4,5,6,7));
            
         } finally {
            //group.shutdownGracefully();
        }
    }
    
    public CloudVarHandler getCloudVarHandler() {
        // Get the handler instance to retrieve the answer.
        return varHandler;
    }
    
    public CloudVarRespHandler getCloudVarRespHandler() {
        // Get the handler instance to retrieve the answer.
        return varRespHandler;
    }
    
    public CloudFuncHandler getCloudFuncHandler() {
        // Get the handler instance to retrieve the answer.
        return funcHandler;
    	
    }
    
    public Channel getChannel() {
    	return ch;
    }
    
    public void shutDown() {
    	group.shutdownGracefully();
    }
}
