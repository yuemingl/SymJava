package symbolic;

public class Multiply extends BinaryOp {
	public Multiply(Expr l, Expr r) {
		if((!(l instanceof SymReal) && !(l instanceof SymInteger)) &&
				(r instanceof SymReal || r instanceof SymInteger)) {
			left = r;
			right = l;
		} else {
			left = l;
			right = r;
		}
		name = left + " * " + right;
	}
	
	public static Expr simplifiedIns(Expr l, Expr r) {
		if(l == Symbol.C1)
			return r;
		else if(r == Symbol.C1)
			return l;
		else if(l == Symbol.C0 || r == Symbol.C0)
			return Symbol.C0;
		else if((l instanceof SymReal<?>) && r instanceof Multiply) {
			Multiply rr = (Multiply)r;
			if(rr.left instanceof SymReal<?>) {
				Number t1 = (Number)((SymReal<?>)l).getVal();
				Number t2 = (Number)((SymReal<?>)rr.left).getVal();
				double coef = t1.doubleValue()*t2.doubleValue();
				if(coef == 1.0) 
					return rr.right;
				return new Multiply(new SymDouble(coef), rr.right);
			}
		}
		return new Multiply(l, r);
	}
	
	@Override
	public Expr subs(Expr from, Expr to) {
		return new Multiply(left.subs(from, to), right.subs(from, to));
	}

	@Override
	public Expr diff(Expr expr) {
		return left.diff(expr).multiply(right).add(left.multiply(right.diff(expr)));
	}

	@Override
	public Expr simplify() {
		return simplifiedIns(left.simplify(), right.simplify());
	}
}
