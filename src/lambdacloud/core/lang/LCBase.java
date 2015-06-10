package lambdacloud.core.lang;

import symjava.symbolic.Expr;

public abstract class LCBase extends Expr {
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
}
