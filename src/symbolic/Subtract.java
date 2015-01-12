package symbolic;

public class Subtract extends BinaryOp {
	public Subtract(Expr l, Expr r) {
		left = l;
		right = r;
		name = left + " - " + right;
	}
	
	@Override
	public Expr subs(Expr from, Expr to) {
		return new Subtract(left.subs(from, to), right.subs(from, to));
	}

	public static Expr simplifiedIns(Expr l, Expr r) {
		if(r == Symbol.C0)
			return l;
		else if(l == Symbol.C0)
			return Symbol.Cm1.multiply(r);
		else
			return new Add(l, r);
	}
	
	@Override
	public Expr diff(Expr expr) {
		return left.diff(expr) + right.diff(expr);
	}
}
