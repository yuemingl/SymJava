package symjava.symbolic;

public class Infinity extends Expr {
	public Infinity() {
		this.label = "oo";
		this.sortKey = label;
	}

	@Override
	public Expr diff(Expr expr) {
		return null;
	}

	@Override
	public Expr simplify() {
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		return false;
	}
}
