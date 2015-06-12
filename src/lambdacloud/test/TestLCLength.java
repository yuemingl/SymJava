package lambdacloud.test;

import static symjava.symbolic.Symbol.x;
import lambdacloud.core.lang.LCAssign;
import lambdacloud.core.lang.LCLength;
import lambdacloud.core.lang.LCStatements;
import symjava.bytecode.BytecodeBatchFunc;
import symjava.symbolic.Expr;
import symjava.symbolic.Symbol;

public class TestLCLength {
	public static void test1() {
		LCStatements lcs = new LCStatements();
		Symbol output = new Symbol("output");
		lcs.append(new LCAssign(output[0], new LCLength(x)));
		BytecodeBatchFunc f = CompileUtils.compileVec(lcs, output, new Expr[]{x});
		double[] out = new double[10];
		double[] xx = new double[] {1,2,3};
		f.apply(out, 0, xx);
		System.out.println(out[0]);
	}
	
	public static void main(String[] args) {
		test1();
	}

}
