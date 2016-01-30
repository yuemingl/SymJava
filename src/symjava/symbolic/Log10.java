package symjava.symbolic;


public class Log10 extends Log {

	public Log10(Expr expr) {
		super(Expr.valueOf(10), expr);
		updateLabel();
	}
	
	public static Expr simplifiedIns(Expr expr) {
		return new Log10(expr);
	}

	@Override
	public void updateLabel() {
		label = "log10(" + arg2 + ")";
		sortKey = label;
	}

}
