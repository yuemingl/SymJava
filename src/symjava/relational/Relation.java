package symjava.relational;

import symjava.symbolic.Expr;

public interface Relation {
	Expr lhs();
	Expr rhs();
}
