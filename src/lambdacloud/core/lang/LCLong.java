package lambdacloud.core.lang;

import symjava.symbolic.TypeInfo;


public class LCLong extends LCVar {
	public LCLong(String name) {
		super(name);
	}
	
	@Override
	public TypeInfo getType() {
		return TYPE.LONG;
	}
}
