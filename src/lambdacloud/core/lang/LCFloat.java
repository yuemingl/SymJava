package lambdacloud.core.lang;

import symjava.symbolic.TypeInfo;


public class LCFloat extends LCVar {
	public LCFloat(String name) {
		super(name);
	}
	
	@Override
	public TypeInfo getType() {
		return TYPE.FLOAT;
	}
}
