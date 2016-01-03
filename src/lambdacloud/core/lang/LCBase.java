package lambdacloud.core.lang;

import symjava.symbolic.Expr;
import symjava.symbolic.TypeInfo;

public abstract class LCBase extends Expr {
	protected LCBase parent = null;
	String indent = "";
	
	@Override
	public Expr simplify() {
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		return false;
	}

	@Override
	public Expr diff(Expr expr) {
		return this;
	}

	@Override
	public TypeInfo getType() {
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
