package symbolic;

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
}
