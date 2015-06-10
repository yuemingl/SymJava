package lambdacloud.core.lang;


public class LCLong extends LCVar {
	public LCLong(String name) {
		super(name);
	}
	
	@Override
	public TYPE getType() {
		return TYPE.LONG;
	}
}
