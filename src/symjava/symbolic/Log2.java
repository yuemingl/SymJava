package symjava.symbolic;

public class Log2 extends Log {

	public Log2(Expr expr) {
		super(Expr.valueOf(2), expr);
		label = "log2(" + expr + ")";
		sortKey = label;
	}
	
	public String toString() {
		return "log2(" + arg2 + ")";
	}

	public static Expr simplifiedIns(Expr expr) {
		return new Log2(expr);
	}
}
