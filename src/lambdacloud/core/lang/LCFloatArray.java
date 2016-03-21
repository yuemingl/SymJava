package lambdacloud.core.lang;

import symjava.symbolic.Expr;

public class LCFloatArray extends LCArray {

	public LCFloatArray(String name) {
		super(name);
	}
	
	public LCFloatArray(LCFloatArray array, int index) {
		super(array + "["+index+"]");
		this.arrayRef = array;
		this.index = Expr.valueOf(index);
	}
	public LCFloatArray(LCFloatArray array, Expr index) {
		super(array + "["+index+"]");
		this.arrayRef = array;
		this.index = index;
	}
	
	@Override
	public LCFloatArray get(int index) {
		return new LCFloatArray(this, index);
	}

	@Override
	public LCFloatArray get(Expr index) {
		return new LCFloatArray(this, index);
	}
}
