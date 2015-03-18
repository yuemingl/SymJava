package symjava.symbolic;

import symjava.symbolic.arity.BinaryOp;

public class Log extends BinaryOp {
	
	/**
	 * Natural logarithm (base e) of expr
	 * @param expr
	 */
	public Log(Expr expr) {
		super(Exp.e, expr);
	}
	
	/**
	 * Log_{base}(expr)
	 * @param base
	 * @param expr
	 */
	public Log(Expr base, Expr expr) {
		super(base, expr);
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
		return simplifiedIns(Exp.e, expr);
	}	
	
	@Override
	public Expr diff(Expr expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expr simplify() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean symEquals(Expr other) {
		// TODO Auto-generated method stub
		return false;
	}

}
