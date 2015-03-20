package symjava.symbolic;

/**
 * An object of SymConst represent a mathematical constant such as PI, E.
 * The constant is displayed as its label but used as a double number 
 * in numerical computation.
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
