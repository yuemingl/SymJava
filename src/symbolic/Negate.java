package symbolic;

import java.util.ArrayList;
import java.util.List;

public class Negate extends UnaryOp {
	
	public Negate(Expr expr) {
		super(expr);
		name = "-" + SymPrinting.addParenthsesIfNeeded(expr, this);
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
	protected void flattenAdd(List<Expr> outList) {
		List<Expr> list1 = new ArrayList<Expr>();
		base.flattenAdd(list1);
		if(list1.size() == 1) { 
			outList.add(this);
			return;
		}
		for(Expr e : list1) {
			outList.add( new Negate(e) );
		}
	}

	@Override
	protected void flattenMultiply(List<Expr> outList) {
		outList.add(this);
	}

}
