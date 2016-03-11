package lambdacloud.net;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class CloudQuery {
	public static final int CLOUD_SD         = 0; // Fetch a CloudSD from cloud
	public static final int CLOUD_FUNC_EVAL  = 1; // Evaluate a function on the cloud
	public static final int TARGET_INFO      = 2; // Query info of target machine on the cloud
	public static final int CLOUD_LIB_INVOKE = 10; // Query info of target machine on the cloud
	

	public int qryType;    // One of the static integer define in this class
	public String objName; // Name of CloudSD or CloudFunc
	
	
	public List<String> argNames = new ArrayList<String>(); // Parameters for function when evaluating
	public String outputName = ""; // Output of a function evaluation
	
	public byte[] getBytes() {
		byte[] bytes = null;
		try {
			int len = 0;
			len += 4;
			
			byte[] nameBytes = objName.getBytes("UTF-8");
			len += 4;
			len += nameBytes.length;
			
			byte[] outputBytes = outputName.getBytes("UTF-8");
			len += 4;
			len += outputBytes.length;

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
			buf.putInt(outputBytes.length);
			buf.put(nameBytes);
			buf.put(outputBytes);
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
		return "CloudQuery: "+qryType+" "+objName;
	}
}
