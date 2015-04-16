package lambdacloud.net;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class CloudResp {
	public int respType;
	public int status;
	public String objName;
	public String message;
	
	public byte[] getBytes() {
		byte[] bytes = null;
		try {
			int len = 0;
			len += 8;
			byte[] nameBytes = objName.getBytes("UTF-8");
			len += 4;
			len += nameBytes.length;
			byte[] messageBytes = message.getBytes("UTF-8");
			len += 4;
			len += messageBytes.length;

			bytes = new byte[len];
			ByteBuffer buf = ByteBuffer.wrap(bytes);
			buf.putInt(respType);
			buf.putInt(status);
			buf.putInt(nameBytes.length);
			buf.putInt(messageBytes.length);
			buf.put(nameBytes);
			buf.put(messageBytes);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return bytes;
	}
	
	public String toString() {
		return objName+":"+String.valueOf(status)+" "+message;
	}
}
