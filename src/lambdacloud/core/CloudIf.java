package lambdacloud.core;

import symjava.symbolic.Expr;

public class CloudIf extends CloudBase {
	public CloudIf(Expr condition, CloudVar ...args) {
		
	}
	
	public CloudIf appendTrue(Expr expr) {
		return this;
	}

	public CloudIf appendFalse(Expr expr) {
		return this;
	}
	
	public void apply() {
		
	}
}
