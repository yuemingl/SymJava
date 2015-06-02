package lambdacloud.core;

import symjava.symbolic.Symbol;

public class CloudLocalVar extends Symbol {

	public CloudLocalVar(String name) {
		super(name);
		this.isDeclaredAsLocal = true;
	}

}
