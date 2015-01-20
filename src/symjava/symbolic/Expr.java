package symjava.symbolic;

import java.util.List;

abstract public class Expr implements Cloneable {
	String label = null;
	String sortKey = null;
	
	//Number of operations for simplifying an expression
	protected int simplifyOps = 0;
	protected boolean simplified = false;
	
	public abstract Expr diff(Expr expr);
	
	public abstract Expr simplify();
	
	public abstract boolean symEquals(Expr other);

	public abstract void flattenAdd(List<Expr> outList);
	
	public abstract void flattenMultiply(List<Expr> outList);
	
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
		return Add.simplifiedIns(this, new SymInteger(other));
	}
	public Expr add(long other) {
		return Add.simplifiedIns(this, new SymLong(other));
	}
	public Expr addRev(long other) {
		return Add.simplifiedIns(this, new SymLong(other));
	}	
	public Expr add(float other) {
		return Add.simplifiedIns(this, new SymFloat(other));
	}
	public Expr addRev(float other) {
		return Add.simplifiedIns(this, new SymFloat(other));
	}	
	public Expr add(double other) {
		return Add.simplifiedIns(this, new SymDouble(other));
	}
	public Expr addRev(double other) {
		return Add.simplifiedIns(this, new SymDouble(other));
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
		return Subtract.simplifiedIns(this, new SymInteger(other));
	}
	public Expr subtract(long other) {
		return Subtract.simplifiedIns(this, new SymLong(other));
	}
	public Expr subtractRev(long other) {
		return Subtract.simplifiedIns(this, new SymLong(other));
	}	
	public Expr subtract(float other) {
		return Subtract.simplifiedIns(this, new SymFloat(other));
	}
	public Expr subtractRev(float other) {
		return Subtract.simplifiedIns(this, new SymFloat(other));
	}
	public Expr subtract(double other) {
		return Subtract.simplifiedIns(this, new SymDouble(other));
	}
	public Expr subtractRev(double other) {
		return Subtract.simplifiedIns(this, new SymDouble(other));
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
		return Multiply.simplifiedIns(this, new SymInteger(other));
	}
	public Expr multiply(long other) {
		return Multiply.simplifiedIns(this, new SymLong(other));
	}
	public Expr multiplyRev(long other) {
		return Multiply.simplifiedIns(this, new SymLong(other));
	}
	public Expr multiply(float other) {
		return Multiply.simplifiedIns(this, new SymFloat(other));
	}
	public Expr multiplyRev(float other) {
		return Multiply.simplifiedIns(this, new SymFloat(other));
	}
	public Expr multiply(double other) {
		return Multiply.simplifiedIns(this, new SymDouble(other));
	}
	public Expr multiplyRev(double other) {
		return Multiply.simplifiedIns(this, new SymDouble(other));
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
		return Divide.simplifiedIns(this, new SymInteger(other));
	}
	public Expr divide(long other) {
		return Divide.simplifiedIns(this, new SymLong(other));
	}
	public Expr divideRev(long other) {
		return Divide.simplifiedIns(this, new SymLong(other));
	}
	public Expr divide(float other) {
		return Divide.simplifiedIns(this, new SymFloat(other));
	}
	public Expr divideRev(float other) {
		return Divide.simplifiedIns(this, new SymFloat(other));
	}
	public Expr divide(double other) {
		return Divide.simplifiedIns(this, new SymDouble(other));
	}
	public Expr divideRev(double other) {
		return Divide.simplifiedIns(this, new SymDouble(other));
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
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public abstract Expr subs(Expr from, Expr to);
	
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
