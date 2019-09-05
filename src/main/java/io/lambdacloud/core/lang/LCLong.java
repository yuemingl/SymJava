package io.lambdacloud.core.lang;

import io.lambdacloud.symjava.symbolic.TypeInfo;


public class LCLong extends LCVar {
	public LCLong(String name) {
		super(name);
	}
	
	@Override
	public TypeInfo getTypeInfo() {
		return TypeInfo.tiLong;
	}
}
