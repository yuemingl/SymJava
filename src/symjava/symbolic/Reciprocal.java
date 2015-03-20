package symjava.symbolic;

import symjava.symbolic.arity.UnaryOp;
import symjava.symbolic.utils.Utils;

public class Reciprocal extends UnaryOp {

	public Reciprocal(Expr base) {
		super(base);
		label =  "1/" +  SymPrinting.addParenthsesIfNeeded(base, this);		
		sortKey = base.getSortKey();
	}
	
	@Override
	public Expr diff(Expr expr) {
		return new Negate(Pow.simplifiedIns(arg,Expr.valueOf(-2))).multiply(arg.diff(expr));
	}

	@Override
	public Expr simplify() {
		if(this.isSimplified)
			return this;
		if(arg instanceof Pow) {
			Pow p = (Pow)arg.simplify();
			p.isSimplified = true;
			Expr rlt = Pow.simplifiedIns(p.arg1, -p.arg2);
			rlt.isSimplified = true;
			return rlt;
		}
		this.isSimplified = true;
		return this;
	}
	
	public static Expr simplifiedIns(Expr expr) {
		if(expr instanceof SymReal<?>) {
			Number n = (Number)((SymReal<?>)expr).getValue();
			return new SymDouble(1.0/n.doubleValue());
		}
		return new Reciprocal(expr);
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Reciprocal) {
			Reciprocal o = (Reciprocal)other;
			return arg.symEquals(o.arg);
		} else if(other instanceof Divide) {
			Divide o = (Divide)other;
			return o.arg1.symEquals(Symbol.C1) && arg.symEquals(o.arg2);
		}
		return false;
	}

	@Override
	public Expr subs(Expr from, Expr to) {
		if(Utils.symCompare(this, from))
			return to;
		if(arg.subs(from,to) == arg) 
			return this;
		return Reciprocal.simplifiedIns(arg.subs(from, to));
	}

}
