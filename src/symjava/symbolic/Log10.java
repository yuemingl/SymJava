package symjava.symbolic;


public class Log10 extends Log {

	public Log10(Expr expr) {
		super(Expr.valueOf(10), expr);
		label = "log10(" + expr + ")";
		sortKey = label;
	}
	
	public String toString() {
		return "log10(" + arg2 + ")";
	}
	
	
	public static Expr simplifiedIns(Expr expr) {
		return new Log10(expr);
	}
}
