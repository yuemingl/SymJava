package symbolic;

public abstract class BinaryOp extends Expr {
	public Expr left;
	public Expr right;
	
	public ExprType getType() {
		return ExprType.BINARY_OP;
	}
}
