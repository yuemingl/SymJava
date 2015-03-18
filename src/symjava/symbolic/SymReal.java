package symjava.symbolic;

import symjava.symbolic.utils.Utils;

public class SymReal<T extends Number> extends Expr {
	protected T value;
	
	public SymReal(T val) {
		this.value = val;
		label = String.valueOf(val);
		sortKey = label;
		isSimplified = true;
	}

	public T getValue() {
		return value;
	}
	
	public double getDoubleValue() {
		return value.doubleValue();
	}

	public int getIntValue() {
		return value.intValue();
	}
	
	public long getLongValue() {
		return value.longValue();
	}
	
	public float getFloatValue() {
		return value.floatValue();
	}
	
	public boolean isInteger() {
		double dval = value.doubleValue();
		double remain = dval - Math.floor(dval);
		if(remain == 0.0) {
			return true;
		}
		return false;
	}

	public boolean isPositive () {
		return value.doubleValue() > 0.0;
	}
	
	public boolean isNonPositive () {
		return value.doubleValue() <= 0.0;
	}
	
	public boolean isNegative () {
		return value.doubleValue() < 0.0;
	}

	public boolean isNonNegative () {
		return value.doubleValue() >= 0.0;
	}

	public boolean isZero () {
		return value.doubleValue() == 0.0;
	}
	
	public boolean isOne () {
		return value.doubleValue() == 1.0;
	}
	
	public boolean isNegativeOne () {
		return value.doubleValue() == -1.0;
	}
	
	public static <T extends Number> SymReal<T> valueOf(T val) {
		return new SymReal<T>(val);
	}

	@Override
	public Expr diff(Expr expr) {
		return Symbol.C0;
	}

	@Override
	public Expr subs(Expr from, Expr to) {
		if(Utils.symCompare(this, from))
			return to;
		return this;
	}

	@Override
	public Expr simplify() {
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof SymReal<?>) {
			SymReal<?> o = (SymReal<?>)other;
			Number t1 = (Number)value;
			Number t2 = (Number)o.getValue();
			//if(t1.equals(t2))
			//	return true;
			if(t1.doubleValue() == t2.doubleValue())
				return true;
		}
		return false;
	}

}
