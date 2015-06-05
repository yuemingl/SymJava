package lambdacloud.core;

import lambdacloud.core.operators.OPAsign;
import lambdacloud.core.operators.OpIndex;
import symjava.symbolic.Expr;
import symjava.symbolic.Symbol;

public class CloudLocalVar extends Symbol {

	public CloudLocalVar(String name) {
		super(name);
		this.isDeclaredAsLocal = true;
	}
	
	public Expr assign(Expr expr) {
		return new OPAsign(this, expr);
	}
	
	public Expr assign(double val) {
		return new OPAsign(this, Expr.valueOf(val));
	}

	public Expr assign(int val) {
		return new OPAsign(this, Expr.valueOf(val));
	}
	
	public Expr get(Expr index) {
		return new OpIndex(this, index);
	}

}
