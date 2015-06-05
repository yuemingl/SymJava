package lambdacloud.core;

public class CloudByte extends CloudVar {
	public CloudByte(String name) {
		super(name);
	}

	@Override
	public TYPE getType() {
		return TYPE.BYTE;
	}
}
