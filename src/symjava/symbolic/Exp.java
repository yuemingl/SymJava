package symjava.symbolic;

import symjava.symbolic.arity.BinaryOp;

public class Exp extends BinaryOp {
	public static SymDouble e = new SymDouble(2.718281828459);
	
	public Exp(Expr arg1, Expr arg2) {
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
