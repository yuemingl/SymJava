package symbolic;

public class Subtract extends BinaryOp {
	public Subtract(Expr l, Expr r) {
		super(l, r);
		name = left + " - " + right;
	}
	
	@Override
	public Expr subs(Expr from, Expr to) {
		return new Subtract(left.subs(from, to), right.subs(from, to));
	}

	public static Expr simplifiedIns(Expr l, Expr r) {
		if(r.symEquals(Symbol.C0))
			return l;
		else if(l.symEquals(Symbol.C0))
			return Symbol.Cm1.multiply(r);
		else if(l instanceof SymReal<?> && r instanceof SymReal<?>) {
			Number t1 = (Number)((SymReal<?>)l).getVal();
			Number t2 = (Number)((SymReal<?>)r).getVal();
			return new SymDouble(t1.doubleValue() - t2.doubleValue());
		} else
			return new Subtract(l, r);
	}
	
	@Override
	public Expr diff(Expr expr) {
		return left.diff(expr) + right.diff(expr);
	}

	@Override
	public Expr simplify() {
		return simplifiedIns(left.simplify(), right.simplify());
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Subtract) {
			Subtract o = (Subtract)other;
			if(	(left.symEquals(o.left) && right.symEquals(o.right)) )
				return true;
		}
		return false;
	}
}
