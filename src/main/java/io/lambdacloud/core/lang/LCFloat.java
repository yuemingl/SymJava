package io.lambdacloud.core.lang;

import io.lambdacloud.symjava.symbolic.TypeInfo;


public class LCFloat extends LCVar {
	public LCFloat(String name) {
		super(name);
	}
	
	@Override
	public TypeInfo getTypeInfo() {
		return TypeInfo.tiFloat;
	}
}
