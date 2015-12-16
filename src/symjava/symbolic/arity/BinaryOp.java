package symjava.symbolic.arity;

import symjava.symbolic.Expr;
import symjava.symbolic.Expr.TYPE;
import symjava.symbolic.utils.Utils;

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

	@Override
	public TYPE getType() {
		return Utils.getConvertedType(arg1.getType(), arg2.getType());
	}
	
	public Expr setArg(int index, Expr arg) {
		if(index == 0) arg1 = arg;
		else if(index == 1) arg2 = arg;
		updateLabel();
		return this;
	}
}
