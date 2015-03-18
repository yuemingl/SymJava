package symjava.symbolic;

import symjava.symbolic.arity.UnaryOp;
import symjava.symbolic.utils.Utils;

public class Cos extends UnaryOp {
	public Cos(Expr arg) {
		super(arg);
		label = "cos(" + arg + ")";
	}

	@Override
	public Expr diff(Expr expr) {
		return Negate.simplifiedIns(Sin.simplifiedIns(expr)).multiply(arg.diff(expr));
	}

	public static Expr simplifiedIns(Expr expr) {
		return new Cos(expr);
	}
	
	@Override
	public Expr simplify() {
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Cos) {
			Utils.symCompare(this.arg, ((Cos) other).arg);
		}
		return false;
	}

}
