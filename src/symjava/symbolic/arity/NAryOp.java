package symjava.symbolic.arity;

import symjava.symbolic.Expr;

public abstract class NAryOp extends Expr {
	public Expr[] args;
	
	public NAryOp(Expr[] args) {
		this.args = args;
	}
	
	@Override
	public Expr[] args() {
		return args;
	}
}
