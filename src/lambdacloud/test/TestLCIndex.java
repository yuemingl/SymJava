package lambdacloud.test;

import static symjava.symbolic.Symbol.*;
import lambdacloud.core.lang.LCAssign;
import lambdacloud.core.lang.LCStatements;
import symjava.bytecode.BytecodeBatchFunc;
import symjava.symbolic.Expr;
import symjava.symbolic.Symbol;

public class TestLCIndex {

	public static void test1() {
		LCStatements lcs = new LCStatements();
		Symbol output = new Symbol("output");
		lcs.append(new LCAssign(output[0], x[2]));
		BytecodeBatchFunc f = CompileUtils.compileVec(lcs, new Expr[]{x});
		double[] out = new double[10];
		double[] xx = new double[] {1,2,3};
		f.apply(out, 0, xx);
		System.out.println(out[0]);
	}

	public static void test2() {
		LCStatements lcs = new LCStatements();
		Symbol output = new Symbol("output");
		lcs.append(new LCAssign(output[0], x[2]*y[2]));
		BytecodeBatchFunc f = CompileUtils.compileVec(lcs, new Expr[]{x, y});
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