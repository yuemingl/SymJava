package lambdacloud.core;

import symjava.symbolic.Expr;

public class CloudIf extends CloudBase {
	public CloudIf(Expr condition, CloudVar ...args) {
		
	}
	
	public CloudIf addTrueBranch(Expr expr, CloudVar output, CloudVar ...args) {
		return this;
	}

	public CloudIf addFalseBranch(Expr expr, CloudVar output, CloudVar ...args) {
		return this;
	}
	
	public void apply() {
		
	}
}
