package symjava.symbolic;

public class SymConst extends Expr {

	public SymConst(String label) {
		this.label = label;
		this.sortKey = label;
	}
	
	@Override
	public Expr diff(Expr expr) {
		return Symbol.C0;
	}

	@Override
	public Expr simplify() {
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		return this.label.equals(other.label);
	}
}
