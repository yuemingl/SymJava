package symjava.symbolic;

import symjava.symbolic.arity.UnaryOp;
import symjava.symbolic.utils.Utils;

public class Tan extends UnaryOp {

	public Tan(Expr arg) {
		super(arg);
		label = "tan(" + arg + ")";
		sortKey = label;
	}

	@Override
	public Expr diff(Expr expr) {
		//1 + tan^2(x) 
		return arg.diff(expr).multiply(new Pow(this, Expr.valueOf(2)).add(1));
	}

	public static Expr simplifiedIns(Expr expr) {
		return new Tan(expr);
	}
	
	@Override
	public Expr simplify() {
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Tan) {
			return Utils.symCompare(this.arg, ((Tan) other).arg);
		}
		return false;
	}
}
