package lambdacloud.net;

import lambdacloud.core.CloudConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.ssl.SslContext;

/**
 * Creates a newly configured {@link ChannelPipeline} for a client-side channel.
 */
public class LambdaClientInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    public LambdaClientInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc(), CloudConfig.getHost(), CloudConfig.getPort()));
        }

        // Enable stream compression (you can remove these two if unnecessary)
        pipeline.addLast(ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
        pipeline.addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));

        // Add the number codec first,
        pipeline.addLast(new CloudVarEncoder());
        pipeline.addLast(new CloudQueryEncoder());
        pipeline.addLast(new CloudFuncEncoder());
        pipeline.addLast(new MessageDecoder());
        
        // and then business logic.
        pipeline.addLast("CloudFuncHandler", new CloudFuncHandler());
        pipeline.addLast("CloudVarHandler", new CloudVarHandler());
        pipeline.addLast("CloudVarRespHandler", new CloudVarRespHandler());
        
    }
}
