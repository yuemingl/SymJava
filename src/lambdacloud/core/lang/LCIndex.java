package lambdacloud.core.lang;

import symjava.symbolic.Expr;

public class LCIndex extends LCBase {
	protected Expr array;
	protected Expr index;
	public LCIndex(Expr array, Expr index) {
		this.array = array;
		this.index = index;
		this.label = array + "["+index+"]";
	}
	
	@Override
	public Expr[] args() {
		return new Expr[]{array, index};
	}

}
