package lambdacloud.examples;

import static symjava.symbolic.Symbol.x;
import static symjava.symbolic.Symbol.y;
import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudVar;
import symjava.symbolic.Expr;

public class TestCloudFuncEval {

	public static void main(String[] args) {
		CloudConfig.setTarget("server1");
		
		double[] data = { 1, 2 };
		CloudVar input = new CloudVar("var123").init(data);
		input.storeToCloud();

		CloudFunc func = new CloudFunc("func123", 
				new Expr[] { x, y }, x + y);
		CloudVar output = new CloudVar();
		func.apply(output, input);

		for (double d : output.fetchToLocal())
			System.out.println(d);
	}
}
