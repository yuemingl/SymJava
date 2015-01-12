package symbolic;

/**
 * 
 * @author yuemingl
 *
 */
public class Divide extends BinaryOp {
	public Divide(Expr numerator, Expr denominator) {
		left = numerator;
		right = denominator;	
		name = left + " / " + right;
	}

	public static Expr simplifiedIns(Expr numerator, Expr denominator) {
		if(numerator == Symbol.C0)
			return Symbol.C0;
		else if(denominator == Symbol.C0)
			throw new IllegalArgumentException("Argument 'divisor' is 0");
		else 
			return new Divide(numerator, denominator);
	}
	
	@Override
	public Expr subs(Expr from, Expr to) {
		return new Divide(left.subs(from, to), right.subs(from, to));
	}

	@Override
	public Expr diff(Expr expr) {
		return left.diff(expr).multiply(right).subtract(left.multiply(right.diff(expr))).
				divide(right.multiply(right));
	}

	@Override
	public Expr simplify() {
		return simplifiedIns(left.simplify(), right.simplify());
	}
}
