package symjava.symbolic;

public class Log2 extends Log {

	public Log2(Expr expr) {
		super(Expr.valueOf(2), expr);
		label = "log2(" + expr + ")";
		sortKey = label;
	}

}
