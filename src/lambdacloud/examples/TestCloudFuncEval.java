package lambdacloud.examples;

import static symjava.math.SymMath.*;
import static symjava.symbolic.Symbol.x;
import static symjava.symbolic.Symbol.y;
import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudVar;
import symjava.symbolic.Expr;

public class TestCloudFuncEval {

	public static void main(String[] args) {
		CloudConfig.setTarget("server1");
		
		double[] data = { 3, 4 };
		CloudVar input = new CloudVar("var123").init(data);
		input.storeToCloud();

		// This function will be sent to cloud
		CloudFunc func = new CloudFunc("func123", 
				new Expr[] { x, y }, sqrt(x*x + y*y));

		CloudVar output = new CloudVar();
		// Evaluate the function on the cloud and 
		// return the reference of the result
		func.apply(output, input); 

		System.out.println(output.getName());
		for (double d : output.fetchToLocal())
			System.out.println(d);
	}
}
