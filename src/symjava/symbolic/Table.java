package symjava.symbolic;

import symjava.symbolic.arity.NaryOp;

/**
 * An expression table is similar to the table of a relational database (RDBMS).
 * The expression table itself is nothing but an array of expressions.
 * 
 * It will return an array of CloudSD when evaluating the expression table.
 * Each element of the returned CloudSD corresponds to a column of a RDBMS table.
 * 
 * The main purpose of an expression table is to 'bind' the values of expressions
 * together into a table. The length of the values must be the same for each expression
 * in the expression table.
 * 
 */
public class Table extends NaryOp {

	public Table(Expr... args) {
		super(args);
	}
	
	public Expr get(int i) {
		return this.args[i];
	}

	@Override
	public Expr simplify() {
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		return false;
	}

	@Override
	public Expr diff(Expr x) {
		return null;
	}

	@Override
	public TypeInfo getTypeInfo() {
		return null;
	}

	@Override
	public void updateLabel() {
		
	}

}
