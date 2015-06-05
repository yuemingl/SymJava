package lambdacloud.core;


public class CloudDouble extends CloudVar {
	public CloudDouble(String name) {
		super(name);
	}

	@Override
	public TYPE getType() {
		return TYPE.DOUBLE;
	}
}
