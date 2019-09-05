package io.lambdacloud.test;

import io.lambdacloud.core.lang.LCArray;
import io.lambdacloud.core.lang.LCAssign;
import io.lambdacloud.core.lang.LCStatements;
import io.lambdacloud.symjava.bytecode.BytecodeVecFunc;
import io.lambdacloud.symjava.symbolic.Expr;

public class TestLCIndex {

	public static void test1() {
		LCStatements lcs = new LCStatements();
		
		LCArray x = LCArray.getDoubleArray("x");
		LCArray output = LCArray.getDoubleArray("output");
		
		lcs.append(new LCAssign(output[0], x[2]));
		BytecodeVecFunc f = CompileUtils.compileVecFunc(lcs, output, x);
		double[] out = new double[10];
		double[] xx = new double[] {1,2,3};
		f.apply(out, 0, xx);
		System.out.println(out[0]);
	}

	public static void test2() {
		LCStatements lcs = new LCStatements();
		
		LCArray x = LCArray.getDoubleArray("x");
		LCArray y = LCArray.getDoubleArray("y");
		LCArray output = LCArray.getDoubleArray("output");
		
		lcs.append(new LCAssign(output[0], x[2]*y[2]));
		BytecodeVecFunc f = CompileUtils.compileVecFunc(lcs, output, x, y);
		double[] out = new double[10];
		double[] xx = new double[] {1,2,3};
		double[] yy = new double[] {2,1,2};
		f.apply(out, 0, xx, yy);
		System.out.println(out[0]);
	}
	
	public static void main(String[] args) {
		test1();
		test2();
	}
	
}
