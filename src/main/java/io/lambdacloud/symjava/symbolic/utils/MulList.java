package io.lambdacloud.symjava.symbolic.utils;

import java.util.ArrayList;
import io.lambdacloud.symjava.symbolic.Expr;
import io.lambdacloud.symjava.symbolic.Negate;

public class MulList extends ArrayList<Expr>{
	private static final long serialVersionUID = 1L;
	int sign = 1;
	
	public MulList() {
		
	}
	
	/**
	 * Flatten the expr to a multiply list
	 * @param expr
	 */
	public MulList(Expr expr) {
		expr.flattenMultiply(this);
		sign = getGlobalSign();
	}
	
	boolean isNegative() {
		return sign == -1;
	}
	
	public int getGlobalSign() {
		int count = 0;
		for(Expr e : this) {
			if(e instanceof Negate) {
				count++;
			}
		}
		if(count%2==1)
			return -1;
		return 1;
	}
	
	/**
	 * Convert the list back to an expr
	 * @return
	 */
	public Expr toExpr() {
		return Utils.multiplyListToExpr(this);
	}
}
