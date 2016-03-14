package lambdacloud.examples;

import lambdacloud.core.lang.LCArray;
import lambdacloud.core.lang.LCInt;
import lambdacloud.core.lang.LCLoop;
import lambdacloud.core.lang.LCStatements;
import lambdacloud.core.lang.LCVar;
import lambdacloud.test.CompileUtils;
import symjava.bytecode.BytecodeVecFunc;
import symjava.relational.Lt;

public class ExampleDotProduct {

	public static void main(String[] args) {
		LCStatements lcs = new LCStatements();
		
		LCArray x = LCArray.getDoubleArray("x");
		LCArray y = LCArray.getDoubleArray("y");
		LCArray output = LCArray.getDoubleArray("output");
		LCInt i = LCVar.getInt("i");
		LCVar sum = LCVar.getDouble("sum");
		
		lcs.append(new LCLoop(i.assign(0), Lt.apply(i, x.getLength()), i.inc())
			.appendBody(sum.assign( sum + x[i]*y[i] )));

		lcs.append(output[2].assign(sum));
		
		BytecodeVecFunc f = CompileUtils.compileVecFunc(lcs, output, x, y);
		
		double[] out = new double[6];
		double[] xx = new double[] {1,2,3,4,5,6};
		double[] yy = new double[] {2,1,2,1,1,1};
		f.apply(out, 0, xx, yy);
		for(double d : out)
			System.out.println(d);
	}

}
