package lambdacloud.core.lang;

import symjava.symbolic.Expr;

public class LCLongArray extends LCArray {

	public LCLongArray(String name) {
		super(name);
	}
	
	public LCLongArray(LCLongArray array, int index) {
		super(array + "["+index+"]");
		this.arrayRef = array;
		this.index = Expr.valueOf(index);
	}
	
	public LCLongArray(LCLongArray array, Expr index) {
		super(array + "["+index+"]");
		this.arrayRef = array;
		this.index = index;
	}
	
	@Override
	public LCLongArray get(int index) {
		return new LCLongArray(this, index);
	}

	@Override
	public LCLongArray get(Expr index) {
		return new LCLongArray(this, index);
	}
}
