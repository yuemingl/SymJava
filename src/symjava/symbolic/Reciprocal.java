package symjava.symbolic;

import java.util.List;

public class Reciprocal extends UnaryOp {

	public Reciprocal(Expr base) {
		super(base);
		label =  "1/" +  SymPrinting.addParenthsesIfNeeded(base, this);		
		sortKey = base.getSortKey();
	}
	
	@Override
	public Expr diff(Expr expr) {
		return base.diff(expr);
	}

	@Override
	public Expr simplify() {
		if(this.simplified)
			return this;
		if(base instanceof Power) {
			Power p = (Power)base.simplify();
			p.simplified = true;
			Expr rlt = new Power(p.base, -p.exponent);
			rlt.simplified = true;
			return rlt;
		}
		this.simplified = true;
		return this;
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
	public void flattenAdd(List<Expr> outList) {
		outList.add(this);
	}

	@Override
	public Expr subs(Expr from, Expr to) {
		return base.subs(from, to);
	}

	@Override
	public void flattenMultiply(List<Expr> outList) {
		outList.add(this);
	}

}
