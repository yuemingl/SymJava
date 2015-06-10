package lambdacloud.core.lang;


public class LCFloat extends LCVar {
	public LCFloat(String name) {
		super(name);
	}
	
	@Override
	public TYPE getType() {
		return TYPE.FLOAT;
	}
}
