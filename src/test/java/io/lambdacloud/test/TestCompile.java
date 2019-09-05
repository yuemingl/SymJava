package io.lambdacloud.test;

import static io.lambdacloud.symjava.symbolic.Symbol.*;
import io.lambdacloud.core.lang.LCArray;
import io.lambdacloud.core.lang.LCDoubleArray;
import io.lambdacloud.core.lang.LCReturn;
import io.lambdacloud.core.lang.LCStatements;
import io.lambdacloud.symjava.bytecode.BytecodeBatchFunc;
import io.lambdacloud.symjava.bytecode.BytecodeVecFunc;
import io.lambdacloud.symjava.symbolic.Expr;
import io.lambdacloud.symjava.symbolic.Vector;
import io.lambdacloud.symjava.symbolic.utils.JIT;

public class TestCompile {

	/**
	 * test comiple, compileVec, compileBatch
	 * @param args
	 */
	public static void main(String[] args) {
		testCompileBatchFunc();
		testCompileVecFunc1();
		testCompileVecFunc2();
	}

	public static void testCompileBatchFunc() {
		BytecodeBatchFunc func = CompileUtils.compileBatchFunc("test1", new Expr[] 
				{
					z,
					y*y,
					x+y+z
				}, x,y,z);
		double[] outAry = new double[3];
		func.apply(outAry, 0, new double[]{1,2,3});
		for(double d : outAry) {
			System.out.println(d);
		}
	}
	
	/**
	 * Use Jama with LCReturn
	 */
	public static void testCompileVecFunc1() {
		int dim = 3;
		Vector a = new Vector("a",dim);
		Vector b = new Vector("b",dim);
		
		BytecodeVecFunc func = CompileUtils.compileVecFunc(new LCReturn(a+b),a,b);
		double[] outAry = new double[dim];
		double[][] args = {
				new double[]{1,2,3},
				new double[]{4,5,6}
		};
		func.apply(outAry, 0, args);
		for(double d : outAry) {
			System.out.println(d);
		}
		
	}
	
	/**
	 * Use Jama with output
	 */
	public static void testCompileVecFunc2() {
		int dim = 3;
		LCStatements lcs = new LCStatements();
		LCArray x = new LCDoubleArray("x");
		LCArray y = new LCDoubleArray("y");
		LCArray output = new LCDoubleArray("outAry1");
		for(int i=0; i<dim; i++) {
			lcs.append( output[i].assign(x[i] + y[i]) );
		}
		BytecodeVecFunc func = CompileUtils.compileVecFunc(lcs, output, x,y);
		double[] outAry = new double[dim];
		double[][] args = {
				new double[]{1,2,3},
				new double[]{4,5,6}
		};
		func.apply(outAry, 0, args);
		for(double d : outAry) {
			System.out.println(d);
		}
		
	}

	/**
	 * Use JIT (use loop)
	 */
	public static void testCompileVecFunc3() {
		BytecodeVecFunc func = JIT.compileVecFunc(new Expr[]{x,y}, x+y);
		int dim = 3;
		double[][] args = {
				new double[]{1,2,3},
				new double[]{4,5,6}
		};
		double[] outAry = new double[dim];
		func.apply(outAry, 0, args);
		for(double d : outAry) {
			System.out.println(d);
		}
	}
	

}
