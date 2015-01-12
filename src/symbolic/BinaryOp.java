package symbolic;

public abstract class BinaryOp extends Expr {
	public Expr left;
	public Expr right;
	
	public BinaryOp(Expr l, Expr r) {
		this.simplifyOps = l.simplifyOps + r.simplifyOps;
		this.left = l;
		this.right = r;
	}
	
	public ExprType getType() {
		return ExprType.BINARY_OP;
	}
}
