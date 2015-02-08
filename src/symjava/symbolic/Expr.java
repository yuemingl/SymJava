package symjava.symbolic;

import java.util.List;

import symjava.symbolic.utils.Utils;

abstract public class Expr implements Cloneable {
	/**
	 * Label(or name) of an expression(Symbol, Func,...)
	 */
	String label = null;
	
	/**
	 * A string used to sort terms in an expression
	 */
	String sortKey = null;
	
	//Number of operations for simplifying an expression
	protected int simplifyOps = 0;
	protected boolean simplified = false;
	
	public abstract Expr diff(Expr expr);
	
	//Functional derivative
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
	
	public abstract Expr simplify();
	
	public abstract boolean symEquals(Expr other);

	public void flattenAdd(List<Expr> outList) {
		outList.add(this);
	};
	
	public void flattenMultiply(List<Expr> outList) {
		outList.add(this);
	}
	
	public boolean isAbstract() {
		return false;
	}
	
	public Expr getExpr() {
		return this;
	}
	
	public String toString() {
		return label;
	}
	
	public String getSortKey() {
		return sortKey;
	}

	public int getSimplifyOps() {
		return simplifyOps;
	}
	public Expr setSimplifyOps(int n) {
		if(n > simplifyOps)
			simplified = true;
		simplifyOps = n;
		return this;
	}
	public Expr incSimplifyOps(int n) {
		simplifyOps += n;
		simplified = true;
		return this;
	}
	public Expr setAsSimplified() {
		simplified = true;
		return this;
	}
	
	/**
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
	 * 
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
	 * 
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
	 * 
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
	 * 
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
	
	public Expr negate() {
		if(this instanceof SymReal<?>) {
			SymReal<?> dd = (SymReal<?>)this;
			double dv = dd.getVal().doubleValue();
			if(dv == 0)
				return Symbol.C0;
			return new SymDouble(-dv);
		}
		return Negate.simplifiedIns(this);
	};
	
	/**
	 * Substitution an expression
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
	
	protected Expr clone() {
		try {
			return (Expr) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
    @Override
    public int hashCode() {
        return this.label.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
       return this.label.equals(((Expr)obj).label);
    }
}
