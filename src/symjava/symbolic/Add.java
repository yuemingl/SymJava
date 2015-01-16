package symjava.symbolic;

import java.util.List;

import symjava.symbolic.utils.Utils;

public class Add extends BinaryOp {
	public Add(Expr l, Expr r) {
		super(l, r);
		label = l + " + " + r;
		sortKey = left.getSortKey()+right.getSortKey();
	}
	
	public static Expr shallowSimplifiedIns(Expr l, Expr r) {
		l = l.simplify();
		r = r.simplify();
		if(Symbol.C0.symEquals(l))
			return r.simplify().incSimplifyOps(1);
		else if(Symbol.C0.symEquals(r))
			return l.simplify().incSimplifyOps(1);
		else if(Utils.symCompare(l, r)) {
			return Symbol.C2.multiply(l).setSimplifyOps(l.getSimplifyOps()+r.getSimplifyOps() + 1);
		} else if(l instanceof SymReal<?> && r instanceof SymReal<?>) {
			Number t1 = (Number)((SymReal<?>)l).getVal();
			Number t2 = (Number)((SymReal<?>)r).getVal();
			return new SymDouble(t1.doubleValue() + t2.doubleValue()).
					setSimplifyOps(l.getSimplifyOps() + r.getSimplifyOps() + 1);
		} else if(l instanceof Negate && r instanceof Negate) {
			Negate nl = (Negate)l;
			Negate nr = (Negate)r;
			return new Negate(Add.simplifiedIns(nl.base, nr.base)).incSimplifyOps(1);
		} else if(l instanceof Negate) {
			Negate nl = (Negate)l;
			return Subtract.simplifiedIns(r, nl.base);
		} else if(r instanceof Negate) {
			Negate nr = (Negate)r;
			return Subtract.simplifiedIns(l, nr.base);
		}
		return new Add(l, r);
	}
	
	public static Expr simplifiedIns(Expr l, Expr r) {
		return Utils.flattenSortAndSimplify(shallowSimplifiedIns(l,r));
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
		return simplifiedIns(left, right);
	}

	public void flattenAdd(List<Expr> outList) {
		left.flattenAdd(outList);
		right.flattenAdd(outList);
	}

	@Override
	public void flattenMultiply(List<Expr> outList) {
		outList.add(this);
	}
		
}
