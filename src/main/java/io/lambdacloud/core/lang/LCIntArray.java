package io.lambdacloud.core.lang;

import io.lambdacloud.symjava.symbolic.Expr;

public class LCIntArray extends LCArray {

	public LCIntArray(String name) {
		super(name);
	}
	
	public LCIntArray(LCIntArray array, int index) {
		super(array + "["+index+"]");
		this.arrayRef = array;
		this.index = Expr.valueOf(index);
	}
	
	public LCIntArray(LCIntArray array, Expr index) {
		super(array + "["+index+"]");
		this.arrayRef = array;
		this.index = index;
	}
	
	@Override
	public LCIntArray get(int index) {
		return new LCIntArray(this, index);
	}

	@Override
	public LCIntArray get(Expr index) {
		return new LCIntArray(this, index);
	}
}
