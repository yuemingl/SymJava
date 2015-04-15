package lambdacloud.net;


import lambdacloud.core.CloudConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
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

    public static void main(String[] args) throws Exception {
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
        } else {
            sslCtx = null;
        }

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new LambdaClientInitializer(sslCtx));

            // Make a new connection.
            ChannelFuture f = b.connect(CloudConfig.getHost(), CloudConfig.getPort()).sync();

            // Get the handler instance to retrieve the answer.
            LambdaClientHandler handler =
                (LambdaClientHandler) f.channel().pipeline().last();
        } finally {
            group.shutdownGracefully();
        }
    }
}
