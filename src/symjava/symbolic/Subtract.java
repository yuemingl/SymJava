package symjava.symbolic;

import java.util.List;

import symjava.symbolic.utils.Utils;

public class Subtract extends BinaryOp {
	public Subtract(Expr l, Expr r) {
		super(l, r);
		label = left + " - " + right;
		sortKey = left.getSortKey()+right.getSortKey();
	}
	
	@Override
	public Expr subs(Expr from, Expr to) {
		return new Subtract(left.subs(from, to), right.subs(from, to));
	}

	public static Expr shallowSimplifiedIns(Expr l, Expr r) {
		l = l.simplify();
		r = r.simplify();
		if(Symbol.C0.symEquals(r))
			return l.incSimplifyOps(1);
		else if(Symbol.C0.symEquals(l))
			return new Negate(r).setSimplifyOps(r.getSimplifyOps() + 1);
		else if(l instanceof SymReal<?> && r instanceof SymReal<?>) {
			Number t1 = (Number)((SymReal<?>)l).getVal();
			Number t2 = (Number)((SymReal<?>)r).getVal();
			return new SymDouble(t1.doubleValue() - t2.doubleValue()).setSimplifyOps(
					l.getSimplifyOps() + r.getSimplifyOps() + 1
					);
		} else if(Utils.symCompare(l, r)) {
			return Symbol.C0.incSimplifyOps(1);
		}
		return new Subtract(l, r);
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
		return simplifiedIns(left, right);
	}

	@Override
	public void flattenAdd(List<Expr> outList) {
		left.flattenAdd(outList);
		new Negate(right).flattenAdd(outList);
	}

	@Override
	public void flattenMultiply(List<Expr> outList) {
		outList.add(this);
	}
}
