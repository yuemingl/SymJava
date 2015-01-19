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
		if(this.isCoeffMulSymbol()) {
			sortKey = this.getSymbolTerm().getSortKey();//+this.getCoeffTerm().getSortKey();
		} else {
			sortKey = left.getSortKey()+right.getSortKey();
		}
	}
	
	public static Expr shallowSimplifiedIns(Expr l, Expr r) {
		int simOps = l.getSimplifyOps() + r.getSimplifyOps() + 1;
		if(Symbol.C1.symEquals(l))
			return r.setSimplifyOps(simOps);
		else if(Symbol.C1.symEquals(r))
			return l.setSimplifyOps(simOps);
		else if(Symbol.C0.symEquals(l) || Symbol.C0.symEquals(r))
			//Here we need a new instance of 0 to hold the number of simplify operations
			return new SymInteger(0).setSimplifyOps(simOps);
		else if(l instanceof SymReal<?> && r instanceof SymReal<?>) {
			Number t1 = (Number)((SymReal<?>)l).getVal();
			Number t2 = (Number)((SymReal<?>)r).getVal();
			return new SymDouble(t1.doubleValue() * t2.doubleValue()).setSimplifyOps(simOps);
		} else if(Symbol.Cm1.symEquals(l)) {
			return new Negate(r).setSimplifyOps(simOps);
		} else if(Symbol.Cm1.symEquals(r)) {
			return new Negate(l).setSimplifyOps(simOps);
		} else if(l instanceof Reciprocal && r instanceof Reciprocal) {
			Reciprocal rl = (Reciprocal)l;
			Reciprocal rr = (Reciprocal)r;
			Expr newBase = simplifiedIns(rl.base, rr.base);
			//? 
			return new Reciprocal( newBase ).setSimplifyOps(simOps + newBase.getSimplifyOps() + 1);
		} else if(l instanceof Reciprocal) {
			Reciprocal rl = (Reciprocal)l;
			return Divide.shallowSimplifiedIns(r, rl.base);
		} else if(r instanceof Reciprocal) {
			Reciprocal rr = (Reciprocal)r;
			return Divide.shallowSimplifiedIns(l, rr.base);
		} else if(l instanceof Power && r instanceof Power) {
			Power lp = (Power)l;
			Power rp = (Power)r;
			if(Utils.symCompare(lp.base, rp.base)) {
				return new Power( lp.base, lp.exponent+rp.exponent).setSimplifyOps(simOps);
			} else if(lp.exponent == rp.exponent) {
				return new Power( simplifiedIns(lp.base, rp.base), lp.exponent).setSimplifyOps(simOps);
			}
		} else if(l instanceof Power) {
			Power lp = (Power)l;
			if(Utils.symCompare(lp.base, r)) {
				return new Power(lp.base, lp.exponent + 1).setSimplifyOps(simOps);
			}
		} else if(r instanceof Power) {
			Power rp = (Power)r;
			if(Utils.symCompare(rp.base, l)) {
				return new Power(rp.base, rp.exponent + 1).setSimplifyOps(simOps);
			}
		}
		if(Utils.symCompare(l, r)) {
			return new Power(l, 2).setSimplifyOps(simOps);
		}
		return new Multiply(l, r).setAsSimplified();
	}
	
	public static Expr simplifiedIns(Expr l, Expr r) {
		return Utils.flattenSortAndSimplify(shallowSimplifiedIns(l, r));
	}
	
	boolean isCoeffMulSymbol() {
		if(left instanceof SymReal<?> && !(right instanceof SymReal<?>) )
			return true;
		if(right instanceof SymReal<?> && !(left instanceof SymReal<?>) )
			return true;
		return false;
	}
	public Expr getCoeffTerm() {
		if(left instanceof SymReal<?> && !(right instanceof SymReal<?>) )
			return left;
		if(right instanceof SymReal<?> && !(left instanceof SymReal<?>) )
			return right;
		return null;
	}
	public Expr getSymbolTerm() {
		if(left instanceof SymReal<?> && !(right instanceof SymReal<?>) )
			return right;
		if(right instanceof SymReal<?> && !(left instanceof SymReal<?>) )
			return left;
		return null;		
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
		if(!this.simplified) {
			return simplifiedIns(left, right);
		}
		return this;
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
