package symjava.symbolic;

import java.util.Map;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.PUSH;

import symjava.symbolic.utils.Utils;

/**
 * A SymReal object is a symbolic representation of a real number
 *
 * @param <T>
 */
public class SymReal<T extends Number> extends Expr {
	protected T value;
	
	public SymReal(T val) {
		this.value = val;
		isSimplified = true;
		updateLabel();
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
		if(this == other)
			return true;
		else if(other instanceof SymReal<?>) {
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
	
	@Override
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		return il.append(new PUSH(cp, value));
	}

	@Override
	public TypeInfo getTypeInfo() {
		if(value instanceof Double)
			return TypeInfo.tiDouble;
		else if(value instanceof Integer)
			return TypeInfo.tiInt;
		else if(value instanceof Long)
			return TypeInfo.tiLong;
		else if(value instanceof Float)
			return TypeInfo.tiFloat;
		else 
			throw new RuntimeException();
	}

	@Override
	public Expr[] args() {
		return new Expr[0];
	}

	@Override
	public void updateLabel() {
		label = String.valueOf(value);
		sortKey = label;
	}	

}
