package lambdacloud.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.math.BigInteger;
import java.util.List;

import lambdacloud.core.CloudVar;

/**
 * Decodes the binary representation of a {@link BigInteger} prepended
 * with a magic number ('F' or 0x46) and a 32-bit integer length prefix into a
 * {@link BigInteger} instance.  For example, { 'F', 0, 0, 0, 1, 42 } will be
 * decoded into new BigInteger("42").
 */
public class MessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // Wait until the length prefix is available.
        if (in.readableBytes() < 9) {
            return;
        }

        in.markReaderIndex();

        // Check the magic number.
        int magicNumber = in.readUnsignedByte();
        if (magicNumber == 'V') { //CloudVar
            if (in.readableBytes() < 8) {
                in.resetReaderIndex();
                return; // Wait until the whole data is available.
            }
            int nameLen = in.readInt();
            int dataLen = in.readInt();
            if (in.readableBytes() < nameLen + dataLen) {
                in.resetReaderIndex();
                return; // Wait until the whole data is available.
            }
            byte[] decoded = new byte[nameLen + dataLen];
            in.readBytes(decoded);
            CloudVar var = NetIOUtils.createCloudVar(decoded, nameLen, dataLen);
            out.add(var);
            //System.out.println("decoded:"+var.getLabel());
        } else if(magicNumber == 'R') { //CloudResp
            if (in.readableBytes() < 16) {
                in.resetReaderIndex();
                return;
            }
        	int respType = in.readInt();
        	int status = in.readInt();
            int nameLen = in.readInt();
            int messageLen = in.readInt();
            if (in.readableBytes() < nameLen + messageLen) {
                in.resetReaderIndex();
                return;
            }
            byte[] decoded = new byte[nameLen + messageLen];
            in.readBytes(decoded);
            if(respType == 1)
            	out.add(NetIOUtils.createCloudVarResp(decoded, status, nameLen, messageLen));
            else if(respType == 2)
            	out.add(NetIOUtils.createCloudFuncResp(decoded, status, nameLen, messageLen));
            	
        } else {
            in.resetReaderIndex();
            throw new CorruptedFrameException("Invalid magic number: " + magicNumber);
        }


    }
}
