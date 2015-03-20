package symjava.relational;

import symjava.symbolic.Expr;

public abstract class Relation {
	public Expr lhs = null;
	public Expr rhs = null;
	
	public String toString() {
		return lhs + " ? " + rhs;
	}
}
