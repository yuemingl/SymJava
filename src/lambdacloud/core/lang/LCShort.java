package lambdacloud.core.lang;

public class LCShort extends LCVar {
	public LCShort(String name) {
		super(name);
	}
	
	@Override
	public TYPE getType() {
		return TYPE.SHORT;
	}
}
