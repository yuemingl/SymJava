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
		CloudVar var = new CloudVar("var123").init(data);
		var.storeToCloud();

		CloudVar output = new CloudVar("output");
		output.resize(1);

		CloudFunc func = new CloudFunc("func123", new Expr[] { x, y }, x + y
				+ 10);
		func.apply(output, var);

		output.fetchToLocal();
		for (double d : output.getAll())
			System.out.println(d);
	}

}
