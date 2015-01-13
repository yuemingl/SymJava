package symbolic;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author yuemingl
 *
 */
public class Divide extends BinaryOp {
	public Divide(Expr numerator, Expr denominator) {
		super(numerator, denominator);
		name = left + " / " + right;
	}

	public static Expr simplifiedIns(Expr numerator, Expr denominator) {
		numerator = numerator.simplify();
		denominator = denominator.simplify();
		if(numerator.symEquals(Symbol.C0))
			return Symbol.C0;
		else if(numerator instanceof SymReal<?> && denominator instanceof SymReal<?>) {
			Number t1 = (Number)((SymReal<?>)numerator).getVal();
			Number t2 = (Number)((SymReal<?>)denominator).getVal();
			return new SymDouble(t1.doubleValue() / t2.doubleValue());
		} else if(denominator.symEquals(Symbol.C0))
			throw new IllegalArgumentException("Argument 'divisor' is 0");
		 else if(denominator.symEquals(Symbol.C1))
			return numerator;
		else 
			return new Divide(numerator, denominator);
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
		return simplifiedIns(left.simplify(), right.simplify());
	}

	@Override
	protected void flattenAdd(List<Expr> outList) {
		List<Expr> list1 = new ArrayList<Expr>();
		left.flattenAdd(list1);
		Reciprocal r = new Reciprocal(right);
		for(Expr e : list1) {
			outList.add( Multiply.simplifiedIns(e, r));
		}
	}

	@Override
	protected void flattenMultiply(List<Expr> outList) {
		left.flattenMultiply(outList);
		new Reciprocal(right).flattenMultiply(outList);
	}

}
