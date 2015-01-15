package symbolic;

import java.util.ArrayList;
import java.util.List;

import symbolic.utils.Utils;

public class Negate extends UnaryOp {
	
	public Negate(Expr expr) {
		super(expr);
		label = "-" + SymPrinting.addParenthsesIfNeeded(expr, this);
		sortKey = base.getSortKey();
	}
	
	@Override
	public Expr diff(Expr expr) {
		return new Negate(base.diff(expr));
	}

	@Override
	public Expr subs(Expr from, Expr to) {
		return new Negate(base.subs(from, to));
	}

	@Override
	public Expr simplify() {
		return new Negate(base.simplify());
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
		//outList.add(this);
	}

}
