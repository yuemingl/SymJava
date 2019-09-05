package io.lambdacloud.symjava.symbolic;

/**
 * A symbol represents infinity
 *
 */
public class Infinity extends Expr {
	public Infinity() {
		updateLabel();
	}

	@Override
	public Expr diff(Expr expr) {
		throw new UnsupportedOperationException();
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
	public Expr[] args() {
		return null;
	}

	@Override
	public TypeInfo getTypeInfo() {
		//TODO
		return null;
	}

	@Override
	public void updateLabel() {
		this.label = "oo";
		this.sortKey = label;
	}
}
