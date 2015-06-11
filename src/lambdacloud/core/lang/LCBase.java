package lambdacloud.core.lang;

import symjava.symbolic.Expr;

public abstract class LCBase extends Expr {
	protected LCBase parent = null;
	String indent = "";
	
	@Override
	public Expr simplify() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean symEquals(Expr other) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Expr diff(Expr expr) {
		throw new UnsupportedOperationException();
	}

	@Override
	public TYPE getType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Expr[] args() {
		return new Expr[0];
	}
	
	public LCBase setParent(LCBase p) {
		this.parent = p;
		return this;
	}
	
	public LCBase indent() {
		this.indent += "    ";
		return this;
	}
	
	public void updateLabel() {
	}
	
	public String toString() {
		updateLabel();
		return label;
	}
}
