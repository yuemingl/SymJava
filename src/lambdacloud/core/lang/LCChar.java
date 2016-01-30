package lambdacloud.core.lang;

import symjava.symbolic.TypeInfo;


public class LCChar extends LCVar {
	public LCChar(String name) {
		super(name);
	}
	
	@Override
	public TypeInfo getTypeInfo() {
		return TypeInfo.tiChar;
	}
}
