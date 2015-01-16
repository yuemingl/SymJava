package symjava.symbolic.utils;

import java.util.ArrayList;
import java.util.List;

import symjava.symbolic.Expr;
import symjava.symbolic.Negate;

public class MulList {
	int sign = 1;
	List<Expr> list = new ArrayList<Expr>();
	public MulList(Expr expr) {
		expr.flattenMultiply(list);
		sign = getGlobalSign();
	}
	
	boolean isNegative() {
		return sign == -1;
	}
	
	public int getGlobalSign() {
		int count = 0;
		for(Expr e : list) {
			if(e instanceof Negate) {
				count++;
			}
		}
		if(count%2==1)
			return -1;
		return 1;
	}
}
