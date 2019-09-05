package io.lambdacloud.symjava.relational;

import io.lambdacloud.symjava.symbolic.Expr;

public interface Relation {
	Expr lhs();
	Expr rhs();
}
