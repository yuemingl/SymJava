package symbolic;

public class Negate extends UnaryOp {
	
	public Negate(Expr expr) {
		this.base = expr;
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

}
