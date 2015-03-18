package symjava.symbolic;

import java.util.List;

import symjava.symbolic.arity.BinaryOp;
import symjava.symbolic.utils.Utils;

public class Pow extends BinaryOp {
	public Expr base;
	public double exponent;
	public Pow(Expr base, double exponent) {
		this.base = base;
		this.exponent = exponent;
		double remain = exponent - Math.floor(exponent);
		String disExp = String.format("%f", this.exponent);
		if(remain == 0.0)
			disExp = String.format("%d", (int)this.exponent);
			
		if(base instanceof Symbol) {
			if(exponent < 0.0)
				label = "("+base + ")^{" + disExp + "}";
			else
				label = base + "^" + disExp + "";
		} else {
			if(exponent < 0.0)
				label = "("+base + ")^{" + disExp + "}";
			else
				label = "("+base + ")^" + disExp;
		}
		sortKey = base.getSortKey()+"power"+String.valueOf(disExp);
	}
	
	public static Expr simplifiedIns(Expr base, double exponent) {
		if(exponent == 0.0 || exponent == -0.0)
			return Symbol.C1;
		else if(base instanceof SymReal<?>) {
			return new SymDouble(Math.pow(((SymReal<?>) base).getVal().doubleValue(), exponent));
		}
		return new Pow(base, exponent);
	}
	
	@Override
	public Expr subs(Expr from, Expr to) {
		if(Utils.symCompare(this, from))
			return to;
		if(base.subs(from,to) == base) 
			return this;
		return Pow.simplifiedIns(base.subs(from, to), exponent);
	}

	@Override
	public Expr diff(Expr expr) {
		if(exponent == 2.0)
			return Symbol.C2.multiply(base).multiply(base.diff(expr));
		else {
			SymDouble i = new SymDouble(exponent);
			return i.multiply(Pow.simplifiedIns(base, exponent - 1)).multiply(base.diff(expr));
		}
	}

	@Override
	public Expr simplify() {
		return Pow.simplifiedIns(base.simplify(), exponent);
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Pow) {
			Pow o = (Pow)other;
			if(base.symEquals(o.base) && exponent == o.exponent)
				return true;
		}
		return false;
	}

	@Override
	public void flattenAdd(List<Expr> outList) {
		//TODO
		//Do we need to do this: (a+b)^2 => a^2 +2ab+b^2
		outList.add(this);
	}

	@Override
	public void flattenMultiply(List<Expr> outList) {
		if(exponent > 0) {
			for(int i=0; i<(int)exponent; i++)
				outList.add(base);
			double remain = exponent - Math.floor(exponent);
			if(remain > 0.0)
				outList.add(new Pow(base, remain));
		} else
			outList.add(this);
	}
}
