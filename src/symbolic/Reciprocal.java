package symbolic;

import java.util.List;

public class Reciprocal extends UnaryOp {

	public Reciprocal(Expr base) {
		super(base);
		if(base instanceof Symbol)
			name = "1/" + base;
		else
			name = "1/(" + base + ")";
	}

	@Override
	public Expr diff(Expr expr) {
		return base.diff(expr);
	}

	@Override
	public Expr simplify() {
		return Divide.simplifiedIns(Symbol.C1, base);
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
	protected void flattenAdd(List<Expr> outList) {
		outList.add(this);
	}

	@Override
	public Expr subs(Expr from, Expr to) {
		return base.subs(from, to);
	}

	@Override
	protected void flattenMultiply(List<Expr> outList) {
		outList.add(this);
	}

}
