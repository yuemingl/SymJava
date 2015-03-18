package symjava.symbolic.arity;

import symjava.symbolic.Expr;

public abstract class TernaryOp extends Expr {
	public Expr arg1;
	public Expr arg2;
	public Expr arg3;
	
	public TernaryOp(Expr arg1, Expr arg2, Expr arg3) {
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.arg3 = arg3;
		setSimplifyOps(
				arg1.getSimplifyOps() + 
				arg2.getSimplifyOps() + 
				arg3.getSimplifyOps()
				);
	}
	
	@Override
	public Expr[] args() {
		return new Expr[] { arg1, arg2, arg3 };
	}
}
