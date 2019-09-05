package io.lambdacloud.core.lang;

import io.lambdacloud.symjava.symbolic.Expr;

public class LCDoubleArray extends LCArray {

	public LCDoubleArray(String name) {
		super(name);
	}
	
	public LCDoubleArray(LCDoubleArray array, int index) {
		super(array + "["+index+"]");
		this.arrayRef = array;
		this.index = Expr.valueOf(index);
	}
	
	public LCDoubleArray(LCDoubleArray array, Expr index) {
		super(array + "["+index+"]");
		this.arrayRef = array;
		this.index = index;
	}

	@Override
	public LCDoubleArray get(int index) {
		return new LCDoubleArray(this, index);
	}

	@Override
	public LCArray get(Expr index) {
		return new LCDoubleArray(this, index);
	}

}
