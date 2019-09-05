package io.lambdacloud.core.lang;

import io.lambdacloud.symjava.symbolic.Expr;

public class LCCharArray extends LCArray {

	public LCCharArray(String name) {
		super(name);
	}
	
	public LCCharArray(LCCharArray array, int index) {
		super(array + "["+index+"]");
		this.arrayRef = array;
		this.index = Expr.valueOf(index);
	}
	
	public LCCharArray(LCCharArray array, Expr index) {
		super(array + "["+index+"]");
		this.arrayRef = array;
		this.index = index;
	}
	
	@Override
	public LCCharArray get(int index) {
		return new LCCharArray(this, index);
	}

	@Override
	public LCCharArray get(Expr index) {
		return new LCCharArray(this, index);
	}
}
