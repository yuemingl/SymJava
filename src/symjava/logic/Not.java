package symjava.logic;

import symjava.symbolic.Expr;
import symjava.symbolic.arity.UnaryOp;

public class Not extends UnaryOp implements Logic {

	public Not(Expr arg) {
		super(arg);
		this.label = "!"+arg;
		this.sortKey = this.label;
	}

	@Override
	public Expr simplify() {
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		return false;
	}

	@Override
	public Expr diff(Expr expr) {
		return this;
	}
	
	public static Expr simplifiedIns(Expr expr) {
		return new Not(expr);
	}
}
