package lambdacloud.core;

public class CloudShort extends CloudVar {
	public CloudShort(String name) {
		super(name);
	}
	
	@Override
	public TYPE getType() {
		return TYPE.SHORT;
	}
}
