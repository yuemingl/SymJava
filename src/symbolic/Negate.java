package symbolic;

public class Negate extends UnaryOp {
	
	public Negate(Expr expr) {
		super(expr);
		name = "-"+expr;
	}
	
	@Override
	public Expr diff(Expr expr) {
		return new Negate(base.diff(expr));
	}

	@Override
	public Expr subs(Expr from, Expr to) {
		return new Negate(base.subs(from, to));
	}

	@Override
	public Expr simplify() {
		return new Negate(base.simplify());
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Negate && base.symEquals(((Negate)other).base))
			return true;
		return false;
	}

}
