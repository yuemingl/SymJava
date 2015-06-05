package lambdacloud.core;


public class CloudLong extends CloudVar {
	public CloudLong(String name) {
		super(name);
	}
	
	@Override
	public TYPE getType() {
		return TYPE.LONG;
	}
}
