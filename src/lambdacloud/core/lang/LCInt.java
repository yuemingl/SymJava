package lambdacloud.core.lang;


public class LCInt extends LCVar {
	public LCInt(String name) {
		super(name);
	}
	
	@Override
	public TYPE getType() {
		return TYPE.INT;
	}
	
	public LCInc inc() {
		return new LCInc(this);
	}
	
	public LCInc inc(int increment) {
		return new LCInc(this, increment);
	}
}
