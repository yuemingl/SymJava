package lambdacloud.net;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class CloudQuery {
	public static final int CLOUD_VAR        = 0;
	public static final int CLOUD_FUNC_EVAL  = 1;
	public static final int TARGET_INFO      = 2;

	public int qryType;
	public String objName;
	public List<String> argNames = new ArrayList<String>();
	
	public byte[] getBytes() {
		byte[] bytes = null;
		try {
			int len = 0;
			len += 4;
			byte[] nameBytes = objName.getBytes("UTF-8");
			len += 4;
			len += nameBytes.length;

			len += 4;
			List<byte[]> argNameBytes = new ArrayList<byte[]>();
			if(argNames.size() != 0) {
				for(String s : argNames) {
					byte[] bs = s.getBytes("UTF-8");
					argNameBytes.add(bs);
					len += 4;
					len += bs.length;
				}
			}

			bytes = new byte[len];
			ByteBuffer buf = ByteBuffer.wrap(bytes);
			buf.putInt(qryType);
			buf.putInt(nameBytes.length);
			buf.put(nameBytes);
			buf.putInt(argNameBytes.size());
			for(byte[] bs : argNameBytes) {
				buf.putInt(bs.length);
				buf.put(bs);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return bytes;
	}
	
	public String toString() {
		return "CloudReq:"+qryType+" "+objName;
	}
}
