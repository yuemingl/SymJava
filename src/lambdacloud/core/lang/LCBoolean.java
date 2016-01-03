package lambdacloud.core.lang;

import symjava.symbolic.TypeInfo;

public class LCBoolean extends LCVar {
	public LCBoolean(String name) {
		super(name);
	}
	
	@Override
	public TypeInfo getType() {
		return TYPE.BOOLEAN;
	}
}
