package lambdacloud.core;


public class CloudChar extends CloudVar {
	public CloudChar(String name) {
		super(name);
	}
	
	@Override
	public TYPE getType() {
		return TYPE.CHAR;
	}	
}
