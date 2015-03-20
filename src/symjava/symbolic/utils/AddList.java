package symjava.symbolic.utils;

import java.util.ArrayList;
import java.util.List;

import symjava.symbolic.Expr;

public class AddList {
	int sign = 1;
	List<Expr> list = new ArrayList<Expr>();
	
	public AddList(Expr expr) {
		expr.flattenAdd(list);
	}
	
	boolean isNegative() {
		return sign == -1;
	}
}