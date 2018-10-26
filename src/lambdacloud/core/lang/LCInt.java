package lambdacloud.core.lang;

import symjava.symbolic.TypeInfo;


public class LCInt extends LCVar {
	public LCInt(String name) {
		super(name);
	}
	
	@Override
	public TypeInfo getTypeInfo() {
		return TypeInfo.tiInt;
	}
	
	public LCInc inc() {
		return new LCInc(this);
	}
	
	public LCInc inc(int increment) {
		return new LCInc(this, increment);
	}
}
