package io.lambdacloud.symjava.symbolic.arity;

import io.lambdacloud.symjava.symbolic.Expr;
import io.lambdacloud.symjava.symbolic.TypeInfo;
import io.lambdacloud.symjava.symbolic.utils.Utils;

public abstract class BinaryOp extends Expr {
	public Expr arg1;
	public Expr arg2;
	
	public BinaryOp(Expr arg1, Expr arg2) {
		this.arg1 = arg1;
		this.arg2 = arg2;
		setSimplifyOps(
				arg1.getSimplifyOps() + 
				arg2.getSimplifyOps()
				);
	}
	
	@Override
	public Expr[] args() {
		return new Expr[] { arg1, arg2 };
	}
	
	public Expr lhs() {
		return arg1;
	}
	
	public Expr rhs() {
		return arg2;
	}

	@Override
	public TypeInfo getTypeInfo() {
		//TODO make it support matrix, vector and tensor?
		TYPE ty = Utils.getConvertedType(arg1.getType(), arg2.getType());
		return new TypeInfo(ty);
	}
	
	public Expr setArg(int index, Expr arg) {
		if(index == 0) arg1 = arg;
		else if(index == 1) arg2 = arg;
		updateLabel();
		return this;
	}
}
