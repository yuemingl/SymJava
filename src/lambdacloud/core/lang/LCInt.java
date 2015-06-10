package lambdacloud.core.lang;


public class LCInt extends LCVar {
	public LCInt(String name) {
		super(name);
	}
	
	@Override
	public TYPE getType() {
		return TYPE.INT;
	}	
}
