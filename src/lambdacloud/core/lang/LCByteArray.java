package lambdacloud.core.lang;

import symjava.symbolic.Expr;

public class LCByteArray extends LCArray {

	public LCByteArray(String name) {
		super(name);
	}
	
	public LCByteArray(LCByteArray array, int index) {
		super(array + "["+index+"]");
		this.arrayRef = array;
		this.index = Expr.valueOf(index);
	}
	public LCByteArray(LCByteArray array, Expr index) {
		super(array + "["+index+"]");
		this.arrayRef = array;
		this.index = index;
	}
	
	@Override
	public LCByteArray get(int index) {
		return new LCByteArray(this, index);
	}

	@Override
	public LCByteArray get(Expr index) {
		return new LCByteArray(this, index);
	}
}
