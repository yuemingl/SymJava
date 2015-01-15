package symbolic;

import java.util.ArrayList;
import java.util.List;

import symbolic.utils.Utils;

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
		numerator = numerator.simplify();
		denominator = denominator.simplify();
		if(Symbol.C0.symEquals(numerator))
			return Symbol.C0.incSimplifyOps(1);
		else if(numerator instanceof SymReal<?> && denominator instanceof SymReal<?>) {
			Number t1 = (Number)((SymReal<?>)numerator).getVal();
			Number t2 = (Number)((SymReal<?>)denominator).getVal();
			return new SymDouble(t1.doubleValue() / t2.doubleValue()).setSimplifyOps(
					numerator.getSimplifyOps() + denominator.getSimplifyOps() + 1
					);
		} else if(denominator.symEquals(Symbol.C0))
			throw new IllegalArgumentException("Argument 'divisor' is 0");
		 else if(Symbol.C1.symEquals(numerator))
			return new Reciprocal(denominator).incSimplifyOps(1);
		 else if(Symbol.C1.symEquals(denominator))
			return numerator.incSimplifyOps(1);
		return new Divide(numerator, denominator);
	}
	
	public static Expr simplifiedIns(Expr numerator, Expr denominator) {
		return Utils.flattenSortAndSimplify(shallowSimplifiedIns(numerator, denominator));
	}
	
	@Override
	public Expr subs(Expr from, Expr to) {
		return new Divide(left.subs(from, to), right.subs(from, to));
	}

	@Override
	public Expr diff(Expr expr) {
		return left.diff(expr).multiply(right).subtract(left.multiply(right.diff(expr))).
				divide(right.multiply(right));
	}

	@Override
	public Expr simplify() {
		return simplifiedIns(left, right);
	}

	@Override
	public void flattenAdd(List<Expr> outList) {
		List<Expr> list1 = new ArrayList<Expr>();
		left.flattenAdd(list1);
		Reciprocal r = new Reciprocal(right);
		for(Expr e : list1) {
			outList.add( new Multiply(e, r) );
		}
	}

	@Override
	public void flattenMultiply(List<Expr> outList) {
		left.flattenMultiply(outList);
		new Reciprocal(right).flattenMultiply(outList);
	}

}
