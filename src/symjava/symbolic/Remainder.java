package symjava.symbolic;

import symjava.symbolic.arity.BinaryOp;

public class Remainder extends BinaryOp {
	public Remainder(Expr arg1, Expr arg2) {
		super(arg1, arg2);
		this.label = arg1+"%"+arg2;
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
		//???
		return this;
	}
}
