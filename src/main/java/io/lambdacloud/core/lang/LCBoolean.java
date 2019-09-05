package io.lambdacloud.core.lang;

import io.lambdacloud.symjava.symbolic.TypeInfo;

public class LCBoolean extends LCVar {
	public LCBoolean(String name) {
		super(name);
	}
	
	@Override
	public TypeInfo getTypeInfo() {
		return TypeInfo.tiBoolean;
	}
}
