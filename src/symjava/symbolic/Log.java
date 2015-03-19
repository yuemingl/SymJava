package symjava.symbolic;

import symjava.math.SymMath;
import symjava.symbolic.arity.BinaryOp;
import symjava.symbolic.utils.Utils;

public class Log extends BinaryOp {
	
	/**
	 * Natural logarithm (base e) of expr
	 * @param expr
	 */
	public Log(Expr expr) {
		super(SymMath.E, expr);
		label = "log(" + expr + ")";
		sortKey = label;
	}
	
	/**
	 * Log_{base}(expr)
	 * @param base
	 * @param expr
	 */
	public Log(Expr base, Expr expr) {
		super(base, expr);
		label = "log_{" + base + "}(" + expr + ")";
		sortKey = label;
	}
	
	public static Expr simplifiedIns(Expr base, Expr expr) {
		if(base instanceof SymReal<?> && expr instanceof SymReal<?>) {
			return new SymDouble(
					Math.log(((SymReal<?>)base).getDoubleValue()) / Math.log(((SymReal<?>)expr).getDoubleValue())
					);
		} else if(expr instanceof SymReal<?>) {
			SymReal<?> realExp = (SymReal<?>)base;
			if(realExp.isOne())
				return Symbol.C0;
		} else if(base instanceof SymReal<?>) {
			SymReal<?> realBase = (SymReal<?>)base;
			if(realBase.isNonPositive())
				throw new RuntimeException("The base of a log cannot be <= 0");
		}
		return new Log(base, expr);
	}
	
	public static Expr simplifiedIns(Expr expr) {
		return new Log(expr);
	}
	
	@Override
	public Expr diff(Expr expr) {
		if(Utils.symCompare(SymMath.E, arg1)) {
			return this.multiply(arg2.diff(expr));
		}
		return null;
	}

	@Override
	public Expr simplify() {
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		return false;
	}

}
