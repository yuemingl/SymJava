package lambdacloud.test;

import lambdacloud.core.lang.LCArray;
import lambdacloud.core.lang.LCLength;
import lambdacloud.core.lang.LCStatements;
import symjava.bytecode.BytecodeVecFunc;
import symjava.symbolic.Expr;

/**
 * 
 * void apply(double[] outAry, int outPos, double[] ...args) {
 * 	outAry[0] = args[0].length;
 * }
 *
 */
public class TestLCLength {
	public static void test1() {
		LCStatements lcs = new LCStatements();
		
		LCArray x = LCArray.getDoubleArray("x");
		LCArray output = LCArray.getDoubleArray("output");
		
		lcs.append(output[0].assign(x.getLength()));
		lcs.append(output[1].assign(x.size()));
		lcs.append(output[2].assign(new LCLength(x)));
		
		BytecodeVecFunc f = CompileUtils.compileVecFunc(lcs, x);
		
		double[] out = new double[10];
		double[] xx = new double[] {1,2,3};
		f.apply(out, 0, xx);
		for(double d : out)
			System.out.println(d);
	}
	
	public static void main(String[] args) {
		test1();
	}

}
