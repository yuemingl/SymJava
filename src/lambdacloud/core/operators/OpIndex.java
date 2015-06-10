package lambdacloud.core.operators;

import lambdacloud.core.CloudBase;
import symjava.symbolic.Expr;

public class OpIndex extends CloudBase {
	protected Expr array;
	protected Expr index;
	public OpIndex(Expr array, Expr index) {
		this.array = array;
		this.index = index;
		this.label = array + "["+index+"]";
	}
	
	@Override
	public Expr[] args() {
		return new Expr[]{array, index};
	}

}
