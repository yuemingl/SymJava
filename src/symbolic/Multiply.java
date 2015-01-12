package symbolic;

import java.util.List;

import symbolic.Utils.Tuple4;

public class Multiply extends BinaryOp {
	public Multiply(Expr l, Expr r) {
		super(l, r);
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
		if(l.symEquals(Symbol.C1))
			return r;
		else if(r.symEquals(Symbol.C1))
			return l;
		else if(l.symEquals(Symbol.C0) || r.symEquals(Symbol.C0))
			return Symbol.C0;
		else if(l.symEquals(r)) {
			Power p = new Power(l, 2);
			p.setSimplifyOps(Math.max(l.getSimplifyOps(), r.getSimplifyOps()) + 1);
			return p;
		} else if((l instanceof SymReal<?>) && r instanceof Multiply) {
			Multiply rr = (Multiply)r;
			if(rr.left instanceof SymReal<?>) {
				Number t1 = (Number)((SymReal<?>)l).getVal();
				Number t2 = (Number)((SymReal<?>)rr.left).getVal();
				double coef = t1.doubleValue()*t2.doubleValue();
				if(coef == 1.0) 
					return rr.right;
				return new Multiply(new SymDouble(coef), rr.right);
			}
		} else if(l instanceof Multiply && r instanceof Multiply) {
			Multiply a1 = (Multiply)l;
			Multiply a2 = (Multiply)r;
			int maxSimplifyOps = -1;
			Expr simplest = null;
			List<Tuple4<Expr>> coms = Utils.C_4_2(a1.left, a1.right, a2.left, a2.right);
			for(Utils.Tuple4<Expr> com : coms) {
				Expr tmp = new Multiply( simplifiedIns(com.o1, com.o2), simplifiedIns(com.o3, com.o4) );
				System.out.println(tmp+"->"+tmp.getSimplifyOps());
				if(tmp.getSimplifyOps() > maxSimplifyOps) {
					maxSimplifyOps = tmp.getSimplifyOps();
					simplest = tmp;
				}
			}
			return simplest;
		} else if(l instanceof Add && r instanceof Multiply) {
			//TODO
		} else if(l instanceof Multiply && r instanceof Add) {
			//TODO
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

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Multiply) {
			Multiply o = (Multiply)other;
			if(	(left.symEquals(o.left) && right.symEquals(o.right)) ||
				(left.symEquals(o.right) && right.symEquals(o.left)) )
				return true;
		}
		return false;
	}
}
