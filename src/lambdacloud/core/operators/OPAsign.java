package lambdacloud.core.operators;

import symjava.symbolic.Expr;
import lambdacloud.core.CloudBase;

public class OPAsign extends CloudBase {
	protected Expr lhs;
	protected Expr rhs;
	public OPAsign(Expr lhs, Expr rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}
	
	public void compile() {
		
	}
	
}
