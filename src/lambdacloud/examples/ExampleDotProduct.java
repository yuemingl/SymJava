package lambdacloud.examples;

import static symjava.symbolic.Symbol.x;
import static symjava.symbolic.Symbol.y;
import lambdacloud.core.lang.LCAssign;
import lambdacloud.core.lang.LCDouble;
import lambdacloud.core.lang.LCInt;
import lambdacloud.core.lang.LCLength;
import lambdacloud.core.lang.LCLoop;
import lambdacloud.core.lang.LCStatements;
import lambdacloud.core.lang.LCVar;
import lambdacloud.test.CompileUtils;
import symjava.bytecode.BytecodeBatchFunc;
import symjava.relational.Lt;
import symjava.symbolic.Expr;
import symjava.symbolic.Symbol;

public class ExampleDotProduct {

	public static void main(String[] args) {
		LCStatements lcs = new LCStatements();
		
		Symbol output = new Symbol("output");
		LCVar i = new LCInt("i");
		LCVar sum = new LCDouble("sum");
		
		lcs.append(new LCLoop(i.assign(0), Lt.apply(i, new LCLength(x)), i.assign(i+1))
			.appendBody(sum.assign(sum + x[i]*y[i])));
		lcs.append(new LCAssign(output[3], sum)); // Store the sum at index 3
		
		BytecodeBatchFunc f = CompileUtils.compileVec(lcs, output, new Expr[]{x, y});
		
		double[] out = new double[6];
		double[] xx = new double[] {1,2,3,4,5,6};
		double[] yy = new double[] {2,1,2,1,1,1};
		f.apply(out, 0, xx, yy);
		for(double d : out)
			System.out.println(d);
	}

}
