package symjava.symbolic;

public class SymRandom extends Expr {
	public SymRandom() {
		this.label = "random()";
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

	@Override
	public TYPE getType() {
		return TYPE.DOUBLE;
	}
	
	@Override
	public Expr[] args() {
		return new Expr[0];
	}	
	
}
