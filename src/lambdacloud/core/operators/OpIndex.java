package lambdacloud.core.operators;

import lambdacloud.core.CloudBase;
import symjava.symbolic.Expr;

public class OpIndex extends CloudBase {
	protected Expr array;
	protected Expr index;
	public OpIndex(Expr array, Expr index) {
		this.array = array;
		this.index = index;
	}
	
	public void compile() {
		
	}
	
}