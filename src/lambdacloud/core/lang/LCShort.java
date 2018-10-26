package lambdacloud.core.lang;

import symjava.symbolic.TypeInfo;

public class LCShort extends LCVar {
	public LCShort(String name) {
		super(name);
	}
	
	@Override
	public TypeInfo getTypeInfo() {
		return TypeInfo.tiShort;
	}
}
