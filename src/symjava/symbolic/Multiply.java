package symjava.symbolic;

import java.util.ArrayList;
import java.util.List;

import symjava.symbolic.utils.Utils;

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
		label =  SymPrinting.addParenthsesIfNeeded(left, this) 
				+ "*" + 
				SymPrinting.addParenthsesIfNeeded(right, this);
		sortKey = left.getSortKey()+right.getSortKey();
	}
	
	public static Expr shallowSimplifiedIns(Expr l, Expr r) {
		l = l.simplify();
		r = r.simplify();
		if(Symbol.C1.symEquals(l))
			return r.incSimplifyOps(1);
		else if(Symbol.C1.symEquals(r))
			return l.incSimplifyOps(1);
		else if(Symbol.C0.symEquals(l) || Symbol.C0.symEquals(r))
			return Symbol.C0.incSimplifyOps(1);
		else if(l instanceof SymReal<?> && r instanceof SymReal<?>) {
			Number t1 = (Number)((SymReal<?>)l).getVal();
			Number t2 = (Number)((SymReal<?>)r).getVal();
			return new SymDouble(t1.doubleValue() * t2.doubleValue()).setSimplifyOps(
					l.getSimplifyOps() + r.getSimplifyOps() + 1
					);
		} else if(Symbol.Cm1.symEquals(l)) {
			return new Negate(r).incSimplifyOps(1);
		} else if(Symbol.Cm1.symEquals(r)) {
			return new Negate(l).incSimplifyOps(1);
		} else if(l instanceof Reciprocal && r instanceof Reciprocal) {
			Reciprocal rl = (Reciprocal)l;
			Reciprocal rr = (Reciprocal)r;
			return new Reciprocal( simplifiedIns(rl.base, rr.base) ).incSimplifyOps(1);
		} else if(l instanceof Reciprocal) {
			Reciprocal rl = (Reciprocal)l;
			return Divide.simplifiedIns(r, rl.base);
		} else if(r instanceof Reciprocal) {
			Reciprocal rr = (Reciprocal)r;
			return Divide.simplifiedIns(l, rr.base);
		} else if(l instanceof Power && r instanceof Power) {
			Power lp = (Power)l;
			Power rp = (Power)r;
			if(Utils.symCompare(lp.base, rp.base)) {
				return new Power( lp.base, lp.exponent+rp.exponent).incSimplifyOps(1);
			} else if(lp.exponent == rp.exponent) {
				return new Power( simplifiedIns(lp.base, rp.base), lp.exponent).incSimplifyOps(1);
			}
		} else if(l instanceof Power) {
			Power lp = (Power)l;
			if(Utils.symCompare(lp.base, r)) {
				return new Power(lp.base, lp.exponent + 1).incSimplifyOps(1);
			}
		} else if(r instanceof Power) {
			Power rp = (Power)r;
			if(Utils.symCompare(rp.base, l)) {
				return new Power(rp.base, rp.exponent + 1).incSimplifyOps(1);
			}
		} else if(Utils.symCompare(l, r)) {
			return new Power(l, 2).setSimplifyOps(l.getSimplifyOps() + r.getSimplifyOps() + 1);
		}
		return new Multiply(l, r);
	}
	
	public static Expr simplifiedIns(Expr l, Expr r) {
		return Utils.flattenSortAndSimplify(shallowSimplifiedIns(l, r));
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
		return simplifiedIns(left, right);
	}

	@Override
	public void flattenAdd(List<Expr> outList) {
		List<Expr> list1 = new ArrayList<Expr>();
		List<Expr> list2 = new ArrayList<Expr>();
		left.flattenAdd(list1);
		right.flattenAdd(list2);
		if(list1.size()==1 && list2.size()==1)
			outList.add(this);
		else {
			for(Expr e1 : list1) {
				for(Expr e2 : list2) {
					outList.add( shallowSimplifiedIns(e1, e2) );
				}
			}
		}
	}

	@Override
	public void flattenMultiply(List<Expr> outList) {
		left.flattenMultiply(outList);
		right.flattenMultiply(outList);
	}
}
