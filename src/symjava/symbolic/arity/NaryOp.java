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
	
	public Expr setArg(int index, Expr arg) {
		args[index] = arg;
		updateLabel();
		return this;
	}	
}
