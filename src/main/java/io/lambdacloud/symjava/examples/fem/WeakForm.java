package io.lambdacloud.symjava.examples.fem;

import io.lambdacloud.symjava.relational.Eq;
import io.lambdacloud.symjava.symbolic.Expr;
import io.lambdacloud.symjava.symbolic.Func;

public class WeakForm extends Eq {
	public Func trial;
	public Func test;
	public WeakForm(Expr lhs, Expr rhs, Func trial, Func test) {
		super(lhs, rhs);
		this.trial = trial;
		this.test = test;
	}
	
	public static WeakForm apply(Expr lhs, Expr rhs, Func trial, Func test) {
		return new WeakForm(lhs, rhs, trial, test);
	}

}
