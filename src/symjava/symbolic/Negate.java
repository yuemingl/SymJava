package symjava.symbolic;

import java.util.ArrayList;
import java.util.List;

import symjava.symbolic.utils.Utils;

public class Negate extends UnaryOp {
	
	public Negate(Expr expr) {
		super(expr);
		label = "-" + SymPrinting.addParenthsesIfNeeded(expr, this);
		sortKey = base.getSortKey();
	}
	
	@Override
	public Expr diff(Expr expr) {
		Expr d = base.diff(expr);
		if(d instanceof SymReal<?>) {
			SymReal<?> dd = (SymReal<?>)d;
			double dv = dd.getVal().doubleValue();
			return new SymDouble(-dv);
		}
		return new Negate(base.diff(expr));
	}

	@Override
	public Expr subs(Expr from, Expr to) {
		return new Negate(base.subs(from, to));
	}

	@Override
	public Expr simplify() {
		if(this.simplified)
			return this;
		Expr nb = base.simplify();
		nb.simplified = true;
		Expr rlt = new Negate(nb);
		rlt.simplified = true;
		return rlt;
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Negate && base.symEquals(((Negate)other).base))
			return true;
		return false;
	}

	@Override
	public void flattenAdd(List<Expr> outList) {
		List<Expr> list1 = new ArrayList<Expr>();
		base.flattenAdd(list1);
		if(list1.size() == 1) { 
			outList.add(this);
		} else {
			for(Expr e : list1) {
				outList.add( new Negate(e) );
			}
		}
	}

	@Override
	public void flattenMultiply(List<Expr> outList) {
		List<Expr> tmp = new ArrayList<Expr>();
		base.flattenMultiply(tmp);
		if(tmp.size() == 1)
			outList.add(this);
		else {
			int sign = Utils.getMultiplyGlobalSign(tmp);
			Utils.removeNegate(tmp);
			if(sign == 1)
				outList.add(new Negate(tmp.get(0)));
			else
				outList.add(tmp.get(0));
			for(int i=1; i<tmp.size(); i++) {
				outList.add(tmp.get(i));
			}
		}
	}

}
