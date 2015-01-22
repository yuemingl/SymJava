package symjava.symbolic;

import java.util.List;

public class Power extends UnaryOp {
	public int exponent;
	public Power(Expr base, int exponent) {
		super(base);
		this.exponent = exponent;
		if(base instanceof Symbol) {
			if(exponent < 0)
				label = "("+base + ")^{" + exponent + "}";
			else
				label = base + "^" + exponent + "";
		} else {
			if(exponent < 0)
				label = "("+base + ")^{" + exponent + "}";
			else
				label = "("+base + ")^" + exponent;
		}
		sortKey = base.getSortKey()+String.valueOf(exponent);
	}
	
	public static Expr simplifiedIns(Expr base, int exponent) {
		if(exponent == 0)
			return Symbol.C1;
		else if(base instanceof SymReal<?>) {
			return new SymDouble(Math.pow(((SymReal<?>) base).getVal().doubleValue(), exponent));
		}
		return new Power(base, exponent);
	}
	
	@Override
	public Expr subs(Expr from, Expr to) {
		if(base.subs(from,to) == base) 
			return this;
		return Power.simplifiedIns(base.subs(from, to), exponent);
	}

	@Override
	public Expr diff(Expr expr) {
		if(exponent == 2)
			return Symbol.C2.multiply(base).multiply(base.diff(expr));
		else {
			SymInteger i = new SymInteger(exponent);
			return i.multiply(Power.simplifiedIns(base, exponent - 1)).multiply(base.diff(expr));
		}
	}

	@Override
	public Expr simplify() {
		return Power.simplifiedIns(base.simplify(), exponent);
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
