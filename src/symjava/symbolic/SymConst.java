package symjava.symbolic;

/**
 * Represent constants such as PI, E
 * 
 */
public class SymConst extends Expr {
	double value;
	
	public SymConst(String label, double value) {
		this.label = label;
		this.sortKey = label;
		this.value = value;
	}
	
	public double getValue() {
		return value;
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
