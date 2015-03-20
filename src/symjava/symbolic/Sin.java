package symjava.symbolic;

import symjava.symbolic.arity.UnaryOp;
import symjava.symbolic.utils.Utils;

public class Sin extends UnaryOp {

	public Sin(Expr arg) {
		super(arg);
		label = "sin(" + arg + ")";
		sortKey = label;
	}

	@Override
	public Expr diff(Expr expr) {
		return new Cos(arg).multiply(arg.diff(expr));
	}

	public static Expr simplifiedIns(Expr expr) {
		return new Sin(expr);
	}
	
	@Override
	public Expr simplify() {
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Sin) {
			return Utils.symCompare(this.arg, ((Sin) other).arg);
		}
		return false;
	}

}
