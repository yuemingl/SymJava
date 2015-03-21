package symjava.relational;

import symjava.symbolic.Expr;
import symjava.symbolic.arity.BinaryOp;

public class Gt extends BinaryOp implements Relation {

	public Gt(Expr arg1, Expr arg2) {
		super(arg1, arg2);
		this.label = arg1 + " > " + arg2;
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
		// TODO Auto-generated method stub
		return null;
	}
	
	public static Gt apply(Expr lhs, Expr rhs) {
		return new Gt(lhs, rhs);
	}
}
