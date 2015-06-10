package lambdacloud.core.lang;


public class LCChar extends LCVar {
	public LCChar(String name) {
		super(name);
	}
	
	@Override
	public TYPE getType() {
		return TYPE.CHAR;
	}	
}
