package symjava.symbolic;

import java.util.ArrayList;
import java.util.List;

import symjava.symbolic.arity.UnaryOp;
import symjava.symbolic.utils.Utils;

public class Negate extends UnaryOp {
	
	public Negate(Expr expr) {
		super(expr);
		label = "-" + SymPrinting.addParenthsesIfNeeded(expr, this);
		sortKey = arg.getSortKey();
	}
	
	@Override
	public Expr diff(Expr expr) {
		Expr d = arg.diff(expr);
		if(d instanceof SymReal<?>) {
			if(d instanceof SymInteger) {
				return new SymInteger(-((SymInteger)d).getVal());
			}
			if(d instanceof SymLong) {
				return new SymLong(-((SymLong)d).getVal());
			}
			SymReal<?> dd = (SymReal<?>)d;
			double dv = dd.getVal().doubleValue();
			if(dv == 0.0)
				return new SymDouble(0.0);
			return new SymDouble(-dv);
		}
		return Negate.simplifiedIns(arg.diff(expr));
	}
	
	public static Expr simplifiedIns(Expr expr) {
		if(expr instanceof SymReal<?>) {
			Number n = ((SymReal<?>)expr).getVal();
			return new SymDouble(-n.doubleValue());
		} else if(expr instanceof Negate) {
			Negate n = (Negate)expr;
			return n.arg;
		}
		return new Negate(expr);
	}

	@Override
	public Expr subs(Expr from, Expr to) {
		if(arg.subs(from,to) == arg) 
			return this;
		return Negate.simplifiedIns(arg.subs(from, to));
	}

	@Override
	public Expr simplify() {
		if(this.isSimplified)
			return this;
		Expr nb = arg.simplify();
		nb.isSimplified = true;
		Expr rlt = new Negate(nb);
		rlt.isSimplified = true;
		return rlt;
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Negate && arg.symEquals(((Negate)other).arg))
			return true;
		return false;
	}

	@Override
	public void flattenAdd(List<Expr> outList) {
		List<Expr> list1 = new ArrayList<Expr>();
		arg.flattenAdd(list1);
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
		arg.flattenMultiply(tmp);
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
