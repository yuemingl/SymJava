package io.lambdacloud.core.lang;

import io.lambdacloud.symjava.symbolic.TypeInfo;


public class LCDouble extends LCVar {
	public LCDouble(String name) {
		super(name);
	}

	@Override
	public TypeInfo getTypeInfo() {
		return TypeInfo.tiDouble;
	}
}
