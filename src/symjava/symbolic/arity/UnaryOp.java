package symjava.symbolic.arity;

import symjava.symbolic.Expr;
import symjava.symbolic.Expr.TYPE;

public abstract class UnaryOp extends Expr {
	public Expr arg;
	
	public UnaryOp(Expr arg) {
		this.arg = arg;
		setSimplifyOps(
				arg.getSimplifyOps()
				);
	}

	@Override
	public Expr[] args() {
		return new Expr[] { arg };
	}
	
	@Override
	public TYPE getType() {
		return arg.getType();
	}
	
	public Expr setArg(int index, Expr arg) {
		this.arg = arg;
		updateLabel();
		return this;
	}
	
}
