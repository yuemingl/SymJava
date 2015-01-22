package symjava.symbolic;

import java.util.ArrayList;
import java.util.List;

import symjava.symbolic.utils.Utils;

/**
 * 
 * @author yuemingl
 *
 */
public class Divide extends BinaryOp {
	public Divide(Expr numerator, Expr denominator) {
		super(numerator, denominator);
		label =  SymPrinting.addParenthsesIfNeeded(left, this) 
				+ "/" + 
				SymPrinting.addParenthsesIfNeeded2(right, this);
		sortKey = left.getSortKey()+right.getSortKey();
	}
	
	public static Expr shallowSimplifiedIns(Expr numerator, Expr denominator) {
		int simOps = numerator.getSimplifyOps() + denominator.getSimplifyOps() + 1;
		if(Symbol.C0.symEquals(numerator))
			return new SymInteger(0).setSimplifyOps(simOps);
		else if(numerator instanceof SymReal<?> && denominator instanceof SymReal<?>) {
			Number t1 = (Number)((SymReal<?>)numerator).getVal();
			Number t2 = (Number)((SymReal<?>)denominator).getVal();
			return new SymDouble(t1.doubleValue() / t2.doubleValue()).setSimplifyOps(simOps);
		} else if(denominator.symEquals(Symbol.C0))
			throw new IllegalArgumentException("Argument 'divisor' is 0");
		 else if(Symbol.C1.symEquals(numerator))
			return Reciprocal.simplifiedIns(denominator).setSimplifyOps(simOps);
		 else if(Symbol.C1.symEquals(denominator))
			return numerator.clone().setSimplifyOps(simOps);
		return new Divide(numerator, denominator).setAsSimplified();
	}
	
	public static Expr simplifiedIns(Expr numerator, Expr denominator) {
		return Utils.flattenSortAndSimplify(shallowSimplifiedIns(numerator, denominator));
	}
	
	@Override
	public Expr subs(Expr from, Expr to) {
		if(Utils.symCompare(this, from))
			return to;
		return new Divide(left.subs(from, to), right.subs(from, to));
	}

	@Override
	public Expr diff(Expr expr) {
		//For debug purpose
		if(expr instanceof Symbol) {
			boolean bl = Utils.containSymbol(left, (Symbol)expr);
			boolean br = Utils.containSymbol(right, (Symbol)expr);
			if(!bl && !br) {
				return Symbol.C0;
			} else if(!bl) {
				return left.multiply(Reciprocal.simplifiedIns(right).diff(expr));
			} else if(!br) {
				return left.diff(expr).multiply(Reciprocal.simplifiedIns(right));
			}
		}
		Expr n0 = left.diff(expr);
		Expr n1 = n0.multiply(right);
		Expr n2 = left.multiply(right.diff(expr));
		Expr n3 = n1.subtract(n2);
		Expr n4 = right.multiply(right);
		Expr n5 = n3.divide(n4);
		return n5;
		
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
		left.flattenAdd(list1);
		Expr r = Reciprocal.simplifiedIns(right);
		for(Expr e : list1) {
			outList.add( new Multiply(e, r) );
		}
	}

	@Override
	public void flattenMultiply(List<Expr> outList) {
		left.flattenMultiply(outList);
		Reciprocal.simplifiedIns(right).flattenMultiply(outList);
	}

}
