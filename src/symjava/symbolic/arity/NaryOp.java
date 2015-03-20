package symjava.symbolic.arity;

import symjava.symbolic.Expr;

public abstract class NaryOp extends Expr {
	public Expr[] args;
	
	public NaryOp(Expr[] args) {
		this.args = args;
	}
	
	@Override
	public Expr[] args() {
		return args;
	}
}
