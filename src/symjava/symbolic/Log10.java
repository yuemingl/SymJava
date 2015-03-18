package symjava.symbolic;

public class Log10 extends Log {

	public Log10(Expr expr) {
		super(Expr.valueOf(10), expr);
		label = "log10(" + expr + ")";
		sortKey = label;
	}

}
