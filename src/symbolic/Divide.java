package symbolic;

/**
 * 
 * @author yuemingl
 *
 */
public class Divide extends BinaryOp {
	public Divide(Expr numerator, Expr denominator) {
		super(numerator, denominator);
		name = left + " / " + right;
	}

	public static Expr simplifiedIns(Expr numerator, Expr denominator) {
		if(numerator.symEquals(Symbol.C0))
			return Symbol.C0;
		else if(denominator.symEquals(Symbol.C0))
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

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Divide) {
			Divide o = (Divide)other;
			if(	(left.symEquals(o.left) && right.symEquals(o.right)) )
				return true;
		}
		return false;
	}
}
