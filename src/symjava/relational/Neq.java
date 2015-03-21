package symjava.relational;

import symjava.symbolic.Expr;
import symjava.symbolic.arity.BinaryOp;

public class Neq extends BinaryOp implements Relation {

	public Neq(Expr arg1, Expr arg2) {
		super(arg1, arg2);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Expr simplify() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean symEquals(Expr other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Expr diff(Expr expr) {
		// TODO Auto-generated method stub
		return null;
	}

}
