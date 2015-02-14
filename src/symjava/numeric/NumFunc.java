package symjava.numeric;

import symjava.symbolic.Expr;

/**
 * An abstract class which can be used to define your own function
 * For example:
		NumFunc<Double> myFun = new NumFunc<Double>() {
			@Override
			public Double apply(double ...args) {
				double x = args[0], y = args[1];
				return x*x + y*y; 
			}
		};
 *
 * @param <T>
 */
public abstract class NumFunc<T> extends Expr {
	public abstract T apply(double ...args);
	
	@Override
	public Expr diff(Expr expr) {
		throw new UnsupportedOperationException("");
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
