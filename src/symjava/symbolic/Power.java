package symjava.symbolic;

import java.util.ArrayList;
import java.util.List;

import symjava.symbolic.utils.Utils;

public class Power extends UnaryOp {
	public int exponent;
	public Power(Expr base, int exponent) {
		super(base);
		this.exponent = exponent;
		if(base instanceof Symbol)
			label = base + "^" + exponent;
		else
			label = "("+base + ")^" + exponent;
		sortKey = base.getSortKey()+String.valueOf(exponent);
	}
	
	@Override
	public Expr subs(Expr from, Expr to) {
		if(base.subs(from,to) == base) 
			return this;
		return new Power(base.subs(from, to), exponent);
	}

	@Override
	public Expr diff(Expr expr) {
		if(exponent == 2)
			return Symbol.C2.multiply(base).multiply(base.diff(expr));
		else {
			SymInteger i = new SymInteger(exponent);
			return i.multiply(new Power(base, exponent - 1)).multiply(base.diff(expr));
		}
	}

	@Override
	public Expr simplify() {
		return new Power(base.simplify(), exponent);
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Power) {
			Power o = (Power)other;
			if(base.symEquals(o.base) && exponent == o.exponent)
				return true;
		}
		return false;
	}

	@Override
	public void flattenAdd(List<Expr> outList) {
//		List<Expr> list = new ArrayList<Expr>();
//		for(int i=0; i<exponent; i++)
//			list.add(this.base);
//		Expr mul = Utils.multiplyListToExpr(list);
//		mul.flattenAdd(outList);
		outList.add(this);
	}

	@Override
	public void flattenMultiply(List<Expr> outList) {
		if(exponent > 0) {
			for(int i=0; i<exponent; i++)
				outList.add(base);
		} else
			outList.add(this);
	}

}
