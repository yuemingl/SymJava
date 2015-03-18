package symjava.symbolic;

import symjava.symbolic.arity.BinaryOp;
import symjava.symbolic.utils.Utils;

public class Sqrt extends BinaryOp {
	public Sqrt(Expr expr) {
		super(expr, Expr.valueOf(2));
		label = "\\sqrt{" + expr + "}";
		sortKey = expr.getSortKey()+"sqrt[2]"+String.valueOf(arg2);
	}
	
	public Sqrt(Expr expr, Expr root) {
		super(expr, root);
		String displayRoot = String.format("%s", this.arg2);
		if(root instanceof SymReal<?>) {
			SymReal<?> realExp = (SymReal<?>)root;
			if(realExp.isInteger()) {
				displayRoot = String.format("%d", realExp.getIntValue());
			}
		}
		label = "\\sqrt["+displayRoot+"]{" + expr + "}";
		//TODO
		sortKey = expr.getSortKey()+"sqrt["+root+"]"+String.valueOf(root);
	}

	@Override
	public Expr diff(Expr expr) {
		return Pow.simplifiedIns(arg1, 1.0/arg2).diff(expr);
	}

	public static Expr simplifiedIns(Expr expr, Expr root) {
		if(expr instanceof SymReal<?> && root instanceof SymReal<?>) {
			return new SymDouble(Math.pow(
					((SymReal<?>)expr).getDoubleValue(), 
					1.0/((SymReal<?>)root).getDoubleValue())
					);
		} else if(expr instanceof SymReal<?>) {
			SymReal<?> realBase = (SymReal<?>)expr;
			if(realBase.isZero())
				return Symbol.C0;
			else if(realBase.isOne())
				return Symbol.C1;
		}else if(root instanceof SymReal<?>) {
			SymReal<?> realExp = (SymReal<?>)root;
			if(realExp.isZero())
				return Symbol.C1;
			else if(realExp.isOne())
				return expr;
			else if(realExp.isNegativeOne())
				return Reciprocal.simplifiedIns(expr);
		}
		return new Sqrt(expr, root);
	}
	
	@Override
	public Expr simplify() {
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Sqrt) {
			Sqrt o = (Sqrt)other;
			if(Utils.symCompare(arg1,  o.arg1) && Utils.symCompare(arg2, o.arg2))
				return true;
		}
		return false;
	}
	
	@Override
	public Expr subs(Expr from, Expr to) {
		if(Utils.symCompare(this, from))
			return to;
		return new Sqrt(arg1.subs(from, to), arg2.subs(from, to));
	}
}
