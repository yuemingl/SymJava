package lambdacloud.core.lang;

import symjava.symbolic.TypeInfo;


public class LCChar extends LCVar {
	public LCChar(String name) {
		super(name);
	}
	
	@Override
	public TypeInfo getType() {
		return TYPE.CHAR;
	}	
}
