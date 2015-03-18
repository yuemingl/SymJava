package symjava.symbolic;

public class Log extends Expr {
	/**
	 * Natural logarithm (base e) of expr
	 * @param expr
	 */
	public Log(Expr expr) {
		
	}
	
	/**
	 * Log_{base}(expr)
	 * @param base
	 * @param expr
	 */
	public Log(Expr base, Expr expr) {
		
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
