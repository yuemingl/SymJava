package symjava.logic;

import symjava.symbolic.Expr;
import symjava.symbolic.arity.BinaryOp;

public class And extends BinaryOp implements Logic {
	
	public And(Expr l, Expr r) {
		super(l, r);
		this.label = l+" & "+r;
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
	
	public static Expr simplifiedIns(Expr lhs, Expr rhs) {
		return new And(lhs, rhs);
	}
}