package lambdacloud.core.lang;


public class LCDouble extends LCVar {
	public LCDouble(String name) {
		super(name);
	}

	@Override
	public TYPE getType() {
		return TYPE.DOUBLE;
	}
}
