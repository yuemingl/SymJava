package symjava.symbolic;

import symjava.symbolic.utils.Utils;

public class Reciprocal extends UnaryOp {

	public Reciprocal(Expr base) {
		super(base);
		label =  "1/" +  SymPrinting.addParenthsesIfNeeded(base, this);		
		sortKey = base.getSortKey();
	}
	
	@Override
	public Expr diff(Expr expr) {
		return new Negate(Power.simplifiedIns(base,-2)).multiply(base.diff(expr));
	}

	@Override
	public Expr simplify() {
		if(this.simplified)
			return this;
		if(base instanceof Power) {
			Power p = (Power)base.simplify();
			p.simplified = true;
			Expr rlt = Power.simplifiedIns(p.base, -p.exponent);
			rlt.simplified = true;
			return rlt;
		}
		this.simplified = true;
		return this;
	}
	
	public static Expr simplifiedIns(Expr expr) {
		if(expr instanceof SymReal<?>) {
			Number n = (Number)((SymReal<?>)expr).getVal();
			return new SymDouble(1.0/n.doubleValue());
		}
		return new Reciprocal(expr);
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Reciprocal) {
			Reciprocal o = (Reciprocal)other;
			return base.symEquals(o.base);
		} else if(other instanceof Divide) {
			Divide o = (Divide)other;
			return o.left.symEquals(Symbol.C1) && base.symEquals(o.right);
		}
		return false;
	}

	@Override
	public Expr subs(Expr from, Expr to) {
		if(Utils.symCompare(this, from))
			return to;
		if(base.subs(from,to) == base) 
			return this;
		return Reciprocal.simplifiedIns(base.subs(from, to));
	}

}
