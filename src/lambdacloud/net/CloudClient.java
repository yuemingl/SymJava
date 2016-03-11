package lambdacloud.net;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

/**
 * 
 * A cloud client represents a connection to a server 
 * with host and port passed to the constructor
 * 
 */
public final class CloudClient {
	public String host;
	public int port;
	
	ChannelFuture f;
	Channel ch;
	EventLoopGroup group;
	
	CloudSDHandler varHandler;
	CloudSDRespHandler varRespHandler;
	CloudFuncHandler funcHandler;
	
	static final boolean SSL = System.getProperty("ssl") != null;

	public CloudClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

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
             .handler(new CloudClientInitializer(sslCtx, host, port));

            // Make a new connection.
            f = b.connect(host, port).sync();
            ch = f.channel();
            varHandler = (CloudSDHandler)f.channel().pipeline().get("CloudVarHandler");
            varRespHandler = (CloudSDRespHandler)f.channel().pipeline().get("CloudVarRespHandler");
            funcHandler = (CloudFuncHandler)f.channel().pipeline().get("CloudFuncHandler");
            
            //ch.writeAndFlush(new CloudVar("xxxxxxx").init(1,2,3,4,5,6,7));
            
         } finally {
            //group.shutdownGracefully();
        }
    }
    
    public CloudSDHandler getCloudVarHandler() {
        // Get the handler instance to retrieve the answer.
        return varHandler;
    }
    
    public CloudSDRespHandler getCloudVarRespHandler() {
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
