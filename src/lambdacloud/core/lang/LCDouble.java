package lambdacloud.core.lang;

import symjava.symbolic.TypeInfo;


public class LCDouble extends LCVar {
	public LCDouble(String name) {
		super(name);
	}

	@Override
	public TypeInfo getType() {
		return TYPE.DOUBLE;
	}
}
