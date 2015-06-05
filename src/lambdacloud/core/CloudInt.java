package lambdacloud.core;


public class CloudInt extends CloudVar {
	public CloudInt(String name) {
		super(name);
	}
	
	@Override
	public TYPE getType() {
		return TYPE.INT;
	}	
}
