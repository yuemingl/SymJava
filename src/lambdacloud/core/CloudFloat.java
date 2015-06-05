package lambdacloud.core;


public class CloudFloat extends CloudVar {
	public CloudFloat(String name) {
		super(name);
	}
	
	@Override
	public TYPE getType() {
		return TYPE.FLOAT;
	}
}
