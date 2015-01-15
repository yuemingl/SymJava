package symbolic;

public abstract class UnaryOp extends Expr {
	public Expr base;
	
	public UnaryOp(Expr base) {
		this.base = base;
		this.simplifyOps = base.simplifyOps;
	}
}
