package lambdacloud.core.lang;

public class LCBoolean extends LCVar {
	public LCBoolean(String name) {
		super(name);
	}
	
	@Override
	public TYPE getType() {
		return TYPE.BOOLEAN;
	}
}
