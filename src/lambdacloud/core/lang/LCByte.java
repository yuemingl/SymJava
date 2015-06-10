package lambdacloud.core.lang;

public class LCByte extends LCVar {
	public LCByte(String name) {
		super(name);
	}

	@Override
	public TYPE getType() {
		return TYPE.BYTE;
	}
}
