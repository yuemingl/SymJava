package lambdacloud.net;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.ssl.SslContext;

/**
 * Creates a newly configured {@link ChannelPipeline} for a client-side channel.
 */
public class CloudClientInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;
    private final String host;
    private final int port;
    
    public CloudClientInitializer(SslContext sslCtx, String host, int port) {
        this.sslCtx = sslCtx;
        this.host = host;
        this.port = port;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc(), host, port));
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
