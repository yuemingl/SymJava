package symjava.symbolic;

import java.util.List;
import java.util.Map;

import lambdacloud.core.lang.LCAssign;
import lambdacloud.core.lang.LCDevice;
import symjava.logic.And;
import symjava.logic.Not;
import symjava.logic.Or;
import symjava.logic.Xor;
import symjava.symbolic.utils.Utils;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;

abstract public class Expr implements Cloneable {
	/**
	 * Label(or name) of an expression
	 */
	protected String label = null;
	
	/**
	 * A string used to sort the terms in an expression
	 */
	protected String sortKey = null;
	
	/**
	 * Number of operations for simplifying an expression
	 */
	public int simplifyOpNum = 0;

	/**
	 * Return true if simplify() is called
	 */
	public boolean isSimplified = false;
	
	/**
	 * Simplify the expression
	 * @return
	 */
	public abstract Expr simplify();
	
	/**
	 * Return true if two expressions are equal in the sense of mathematics
	 * @param other
	 * @return
	 */
	public abstract boolean symEquals(Expr other);
	
	/**
	 * Return the arguments of the expression
	 * @return
	 */
	public abstract Expr[] args();// { return new Expr[0]; }

	/**
	 * Derivative of the expression with respect to x
	 * @param x
	 * @return
	 */
	public abstract Expr diff(Expr x);
	
	/**
	 * Functional derivative of f with respect to df
	 * @param f
	 * @param df
	 * @return
	 */
	public Expr fdiff(Expr f, Expr df) {
		if(!(f instanceof Func) || !(df instanceof Func))
			throw new IllegalArgumentException();
		Func F = (Func)f;
		Symbol alpha = new Symbol("_alpha_");
		Expr ff = this.subs(f, f.add(alpha.multiply(df)));
		Expr dff = ff.diff(alpha);
		if(Symbol.C0.symEquals(dff)) {
			Func ret = new Func("0", F.args);
			ret.expr = Symbol.C0;
			return ret;
		}
		return dff.subs(alpha, 0).simplify();
	}
	
	/**
	 * Split the terms into a list for Add and Subtract
	 * @param outList
	 */
	public void flattenAdd(List<Expr> outList) {
		outList.add(this);
	};

	/**
	 * Split the terms into a list for Multiply and Divide
	 * @param outList
	 */
	public void flattenMultiply(List<Expr> outList) {
		outList.add(this);
	}
	
	/**
	 * Return true if the expression is an abstract thing.
	 * for example, a abstract function
	 * @return
	 */
	public boolean isAbstract() {
		return false;
	}
	
	/**
	 * Return the string representation of the expression
	 */
	public String toString() {
		return label;
	}
	
	/**
	 * Return the LaTex representation of the expression
	 * @return
	 */
	public String toLaTex() {
		return label;
	}
	
	/**
	 * Set the label(or name) of the expression
	 * @param label
	 * @return
	 */
	public Expr setLabel(String label) {
		this.label = label;
		return this;
	}
	
	/**
	 * Return the label(or name) of the expression
	 * @return
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Set a string key for sorting the terms in the expression
	 * @param sortKey
	 * @return
	 */
	public Expr setSortKey(String sortKey) {
		this.sortKey = sortKey;
		return this;
	}
	
	/**
	 * Get the string key used to sort the terms in the expression
	 * @param sortKey
	 * @return
	 */
	public String getSortKey() {
		return sortKey;
	}

	/**
	 * Count number of operations for simplification
	 * @return
	 */
	public int getSimplifyOps() {
		return simplifyOpNum;
	}
	public Expr setSimplifyOps(int n) {
		//Make no sense, call setAsSimplified() explicitly
		//if(n > simplifyOpNum)
		//	isSimplified = true;
		simplifyOpNum = n;
		return this;
	}
	public Expr incSimplifyOps(int n) {
		simplifyOpNum += n;
		isSimplified = true;
		return this;
	}
	public Expr setAsSimplified() {
		isSimplified = true;
		return this;
	}
	
	/**
	 * Operator overloading support:
	 * Expr a = 5;
	 * 
	 * @param v
	 * @return
	 */
	public static Expr valueOf(int v) {
		return new SymInteger(v);
	}
	public static Expr valueOf(long v) {
		return new SymLong(v);
	}
	public static Expr valueOf(float v) {
		return new SymFloat(v);
	}
	public static Expr valueOf(double v) {
		return new SymDouble(v);
	}
	/**
	 * Operator overloading support:
	 * a+b
	 * @param other
	 * @return
	 */
	public Expr add(Expr other) {
		return Add.simplifiedIns(this, other);
	}
	public Expr add(int other) {
		return Add.simplifiedIns(this, new SymInteger(other));
	}
	public Expr addRev(int other) {
		return Add.simplifiedIns(new SymInteger(other), this);
	}
	public Expr add(long other) {
		return Add.simplifiedIns(this, new SymLong(other));
	}
	public Expr addRev(long other) {
		return Add.simplifiedIns(new SymLong(other), this);
	}	
	public Expr add(float other) {
		return Add.simplifiedIns(this, new SymFloat(other));
	}
	public Expr addRev(float other) {
		return Add.simplifiedIns(new SymFloat(other), this);
	}	
	public Expr add(double other) {
		return Add.simplifiedIns(this, new SymDouble(other));
	}
	public Expr addRev(double other) {
		return Add.simplifiedIns(new SymDouble(other), this);
	}
	
	/**
	 * Operator overloading support:
	 * a-b
	 * @param other
	 * @return
	 */
	public Expr subtract(Expr other) {
		return Subtract.simplifiedIns(this, other);
	}
	public Expr subtract(int other) {
		return Subtract.simplifiedIns(this, new SymInteger(other));
	}
	public Expr subtractRev(int other) {
		return Subtract.simplifiedIns(new SymInteger(other), this);
	}
	public Expr subtract(long other) {
		return Subtract.simplifiedIns(this, new SymLong(other));
	}
	public Expr subtractRev(long other) {
		return Subtract.simplifiedIns(new SymLong(other), this);
	}	
	public Expr subtract(float other) {
		return Subtract.simplifiedIns(this, new SymFloat(other));
	}
	public Expr subtractRev(float other) {
		return Subtract.simplifiedIns(new SymFloat(other), this);
	}
	public Expr subtract(double other) {
		return Subtract.simplifiedIns(this, new SymDouble(other));
	}
	public Expr subtractRev(double other) {
		return Subtract.simplifiedIns(new SymDouble(other), this);
	}
	
	/**
	 * Operator overloading support:
	 * a*b
	 * @param other
	 * @return
	 */
	public Expr multiply(Expr other) {
		return Multiply.simplifiedIns(this, other);
	}
	public Expr multiply(int other) {
		return Multiply.simplifiedIns(this, new SymInteger(other));
	}
	public Expr multiplyRev(int other) {
		return Multiply.simplifiedIns(new SymInteger(other), this);
	}
	public Expr multiply(long other) {
		return Multiply.simplifiedIns(this, new SymLong(other));
	}
	public Expr multiplyRev(long other) {
		return Multiply.simplifiedIns(new SymLong(other), this);
	}
	public Expr multiply(float other) {
		return Multiply.simplifiedIns(this, new SymFloat(other));
	}
	public Expr multiplyRev(float other) {
		return Multiply.simplifiedIns(new SymFloat(other), this);
	}
	public Expr multiply(double other) {
		return Multiply.simplifiedIns(this, new SymDouble(other));
	}
	public Expr multiplyRev(double other) {
		return Multiply.simplifiedIns(new SymDouble(other), this);
	}
	
	/**
	 * Operator overloading support:
	 * a/b
	 * @param other
	 * @return
	 */
	public Expr divide(Expr other) {
		return Divide.simplifiedIns(this, other);
	}	
	public Expr divide(int other) {
		return Divide.simplifiedIns(this, new SymInteger(other));
	}
	public Expr divideRev(int other) {
		return Divide.simplifiedIns(new SymInteger(other), this);
	}
	public Expr divide(long other) {
		return Divide.simplifiedIns(this, new SymLong(other));
	}
	public Expr divideRev(long other) {
		return Divide.simplifiedIns(new SymLong(other), this);
	}
	public Expr divide(float other) {
		return Divide.simplifiedIns(this, new SymFloat(other));
	}
	public Expr divideRev(float other) {
		return Divide.simplifiedIns(new SymFloat(other), this);
	}
	public Expr divide(double other) {
		return Divide.simplifiedIns(this, new SymDouble(other));
	}
	public Expr divideRev(double other) {
		return Divide.simplifiedIns(new SymDouble(other), this);
	}
	
	/**
	 * Operator overloading support:
	 * -a
	 * 
	 */
	public Expr negate() {
		if(this instanceof SymReal<?>) {
			SymReal<?> dd = (SymReal<?>)this;
			double dv = dd.getValue().doubleValue();
			if(dv == 0)
				return Symbol.C0;
			return new SymDouble(-dv);
		}
		return Negate.simplifiedIns(this);
	};
	
	/**
	 * x%y
	 * @return
	 */
	public Expr remainder(Expr other) {
		return new Remainder(this, other);
	}
	
	/*
	 * !x
	 */
	public Expr not() {
		return Not.simplifiedIns(this);
	}
	
	/**
	 * x&y
	 * @param other
	 * @return
	 */
	public Expr and(Expr other) {
		return And.simplifiedIns(this, other);
	}
	
	/**
	 * x|y
	 * @param other
	 * @return
	 */
	public Expr or(Expr other) {
		return Or.simplifiedIns(this, other);
	}
	
	/**
	 * x^y
	 * @param other
	 * @return
	 */
	public Expr xor(Expr other) {
		return Xor.simplifiedIns(this, other);
	}
	
//	/**
//	 * TODO We cannot use the comparison Operator overloading in java-oo for our use case
//	 * @param other
//	 * @return
//	 */
//	public int compareTo(Expr other) {
//		if(Ge.stackTop == null)
//			Ge.stackTop = Ge.apply(this, other); //fix: use push()
//		else
//			Ge.stackTop = Ge.apply(Ge.stackTop, other);
//		return -1;
//	}
	
	
	/**
	 * Substitution
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public Expr subs(Expr from, Expr to) {
		if(Utils.symCompare(this, from)) {
			return to;
		}
		return this;
	}
	
	public Expr subs(Expr from, int to) {
		return subs(from, new SymInteger(to));
	}
	public Expr subs(Expr from, long to) {
		return subs(from, new SymLong(to));
	}
	public Expr subs(Expr from, float to) {
		return subs(from, new SymFloat(to));
	}
	public Expr subs(Expr from, double to) {
		return subs(from, new SymDouble(to));
	}
	
	public Expr clone() {
		try {
			return (Expr) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * If the labels of two expressions are the same they are considered as
	 * the same symbolic expressions 
	 */
    @Override
    public int hashCode() {
        return this.label.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
       return this.label.equals(((Expr)obj).label);
    }
    
//	/**
//	 * Assign the result of evaluation of the expression to a local variable
//	 * when compiling. The local variable must be a symbol which is declared 
//	 * as a local variable.
//	 * <p>
//	 * The call of this function can be understand as
//	 * Symbol symLocal; //Declared somewhere
//	 * symLocal = this;
//	 * 
//	 * <p>
//	 * Note: The name of a symbol is a global name. Make sure you don't have 
//	 * two symbols with the same name, otherwise they are treated as the same
//	 * symbol or local variable in compiled code.
//	 * 
//	 * @param symLocal A symbol declared as a local variable
//	 * @return An instance of operator OPAsign
//	 */
//	public Expr assignTo(Symbol symLocal) {
//		return new OPAsign(symLocal, this);
//	}
	
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		throw new UnsupportedOperationException();//il.append(InstructionConstants.NOP);
	}
	
	public enum TYPE {INT, LONG, FLOAT, DOUBLE, BOOLEAN, BYTE, CHAR, SHORT, VOID,
		MATRIX, VECTOR, TENSOR};
	
	public abstract TYPE getType();
	
	public Expr assign(Expr expr) {
		return new LCAssign(this, expr);
	}
	
	public Expr assign(double val) {
		return new LCAssign(this, Expr.valueOf(val));
	}

	public Expr assign(int val) {
		return new LCAssign(this, Expr.valueOf(val));
	}	
	
	LCDevice device = null;
	public Expr runOn(LCDevice dev) {
		device = dev;
		return this;
	}
	
	public LCDevice getDevice() {
		return device;
	}
	
	public Expr setArg(int index, Expr arg) {
		throw new UnsupportedOperationException();
	}
	
	abstract public void updateLabel();
}

