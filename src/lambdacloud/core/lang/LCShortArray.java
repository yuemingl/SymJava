package lambdacloud.core.lang;

import symjava.symbolic.Expr;

public class LCShortArray extends LCArray {

	public LCShortArray(String name) {
		super(name);
	}
	
	public LCShortArray(LCShortArray array, int index) {
		super(array + "["+index+"]");
		this.arrayRef = array;
		this.index = index;
	}
	
	public LCShortArray(LCShortArray array, Expr index) {
		super(array + "["+index+"]");
		this.arrayRef = array;
		this.index = index;
	}
	
	@Override
	public LCShortArray get(int index) {
		return new LCShortArray(this, index);
	}

	@Override
	public LCShortArray get(Expr index) {
		return new LCShortArray(this, index);
	}
}
