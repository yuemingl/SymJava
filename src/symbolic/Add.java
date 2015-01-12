package symbolic;

public class Add extends BinaryOp {
	public Add(Expr l, Expr r) {
		left = l;
		right = r;
		name = l + " + " + r;
	}
	
	public static Expr simplifiedIns(Expr l, Expr r) {
		if(l == Symbol.C0)
			return r;
		else if(r == Symbol.C0)
			return l;
		else if(l == r)
			return Symbol.C2.multiply(l);
		else if(l instanceof SymReal<?> && r instanceof SymReal<?>) {
			Number t1 = (Number)((SymReal<?>)l).getVal();
			Number t2 = (Number)((SymReal<?>)r).getVal();
			return new SymDouble(t1.doubleValue() + t2.doubleValue());
		}
		return new Add(l, r);
	}

	@Override
	public Expr subs(Expr from, Expr to) {
		return new Add(left.subs(from, to), right.subs(from, to));
	}

	@Override
	public Expr diff(Expr expr) {
		return left.diff(expr) + right.diff(expr);
	}

	@Override
	public Expr simplify() {
		return simplifiedIns(left.simplify(), right.simplify());
	}
}
