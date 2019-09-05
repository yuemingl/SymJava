package io.lambdacloud.symjava.symbolic.utils;

import java.util.ArrayList;
import io.lambdacloud.symjava.symbolic.Expr;

public class AddList extends ArrayList<Expr>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	int sign = 1;
	
	public AddList() {
		
	}
	
	/**
	 * Flatten the expr to a summation list
	 * @param expr
	 */
	public AddList(Expr expr) {
		expr.flattenAdd(this);
	}
	
	boolean isNegative() {
		return sign == -1;
	}

	/**
	 * Convert the list back to an expr
	 * @return
	 */
	public Expr toExpr() {
		return Utils.addListToExpr(this);
	}
}