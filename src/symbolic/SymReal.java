package symbolic;

import java.util.List;

public class SymReal<T extends Number> extends Expr {
	T val;
	
	public SymReal(T val) {
		this.val = val;
		name = String.valueOf(val);
	}

	public T getVal() {
		return val;
	}
	
	public static <T extends Number> SymReal<T> valueOf(T val) {
		return new SymReal<T>(val);
	}

	@Override
	public Expr diff(Expr expr) {
		return Symbol.C0;
	}

	@Override
	public Expr subs(Expr from, Expr to) {
		return this;
	}

	@Override
	public Expr simplify() {
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof SymReal<?>) {
			SymReal<?> o = (SymReal<?>)other;
			Number t1 = (Number)val;
			Number t2 = (Number)o.getVal();
			if(t1.doubleValue() == t2.doubleValue())
				return true;
		}
		return false;
	}

	@Override
	protected void flattenAdd(List<Expr> outList) {
		outList.add(this);
	}

	@Override
	protected void flattenMultiply(List<Expr> outList) {
		outList.add(this);
	}
}
