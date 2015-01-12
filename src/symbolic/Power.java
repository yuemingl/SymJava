package symbolic;

public class Power extends UnaryOp {
	public int exponent;
	public Power(Expr base, int exponent) {
		super(base);
		this.exponent = exponent;
		name = "("+base + ")^" + exponent;
	}
	
	@Override
	public Expr subs(Expr from, Expr to) {
		return new Power(base.subs(from, to), exponent);
	}

	@Override
	public Expr diff(Expr expr) {
		SymInteger i = new SymInteger(exponent);
		return i.multiply(new Power(base, exponent - 1)).multiply(base.diff(expr));
	}

	@Override
	public Expr simplify() {
		return new Power(base.simplify(), exponent);
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Power) {
			Power o = (Power)other;
			if(base.symEquals(o.base) && exponent == o.exponent)
				return true;
		}
		return false;
	}
}
