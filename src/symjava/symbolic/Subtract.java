package symjava.symbolic;

import java.util.List;

import symjava.symbolic.utils.Utils;

public class Subtract extends BinaryOp {
	public Subtract(Expr l, Expr r) {
		super(l, r);
		if(right instanceof Add || right instanceof Subtract)
			label = left + " - (" + right + ")";
		else
			label = left + " - " + right;

		sortKey = left.getSortKey()+right.getSortKey();
	}
	
	@Override
	public Expr subs(Expr from, Expr to) {
		if(Utils.symCompare(this, from))
			return to;
		return new Subtract(left.subs(from, to), right.subs(from, to));
	}

	public static Expr shallowSimplifiedIns(Expr l, Expr r) {
		int simOps = l.getSimplifyOps() + r.getSimplifyOps() + 1;
		if(Utils.symCompare(l, r)) {
			return new SymInteger(0).setSimplifyOps(simOps);
		} else if(l instanceof SymReal<?> && r instanceof SymReal<?>) {
			if(l instanceof SymInteger && r instanceof SymInteger) {
				SymInteger il = (SymInteger)l;
				SymInteger ir = (SymInteger)r;
				return new SymInteger(il.getVal()-ir.getVal()).setSimplifyOps(simOps);
			} else if(l instanceof SymLong && r instanceof SymLong) {
				SymLong il = (SymLong)l;
				SymLong ir = (SymLong)r;
				return new SymLong(il.getVal()-ir.getVal()).setSimplifyOps(simOps);
			}
			Number t1 = (Number)((SymReal<?>)l).getVal();
			Number t2 = (Number)((SymReal<?>)r).getVal();
			return new SymDouble(t1.doubleValue() - t2.doubleValue()).setSimplifyOps(simOps);
		} else if(Symbol.C0.symEquals(r))
			return l.clone().setSimplifyOps(simOps);
		else if(Symbol.C0.symEquals(l))
			return new Negate(r).setSimplifyOps(simOps);
		return new Subtract(l, r).setAsSimplified();
	}
	
	public static Expr simplifiedIns(Expr l, Expr r) {
		return Utils.flattenSortAndSimplify(shallowSimplifiedIns(l,r));
	}
	
	@Override
	public Expr diff(Expr expr) {
		return left.diff(expr).subtract(right.diff(expr));
	}

	@Override
	public Expr simplify() {
		if(!this.simplified) {
			return simplifiedIns(left, right);
		}
		return this;
	}

	@Override
	public void flattenAdd(List<Expr> outList) {
		left.flattenAdd(outList);
		new Negate(right).flattenAdd(outList);
	}
}
