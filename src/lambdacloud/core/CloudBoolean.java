package lambdacloud.core;

public class CloudBoolean extends CloudVar {
	public CloudBoolean(String name) {
		super(name);
	}
	
	@Override
	public TYPE getType() {
		return TYPE.BOOLEAN;
	}
}
