package symjava.symbolic.arity;

import symjava.symbolic.Expr;

public abstract class BinaryOp extends Expr {
	public Expr arg1;
	public Expr arg2;
	
	public BinaryOp(Expr arg1, Expr arg2) {
		this.arg1 = arg1;
		this.arg2 = arg2;
		setSimplifyOps(
				arg1.getSimplifyOps() + 
				arg2.getSimplifyOps()
				);
	}
	
	@Override
	public Expr[] args() {
		return new Expr[] { arg1, arg2 };
	}
	
	public Expr lhs() {
		return arg1;
	}
	
	public Expr rhs() {
		return arg2;
	}	
}
