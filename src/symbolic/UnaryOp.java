package symbolic;

public abstract class UnaryOp extends Expr {
	public Expr base;
	
	public ExprType getType() {
		return ExprType.UNARY_OP;
	}
}
