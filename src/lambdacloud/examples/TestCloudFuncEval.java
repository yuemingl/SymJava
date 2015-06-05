package lambdacloud.examples;

import static symjava.math.SymMath.*;
import static symjava.symbolic.Symbol.x;
import static symjava.symbolic.Symbol.y;
import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudSharedVar;
import symjava.symbolic.Expr;

public class TestCloudFuncEval {

	public static void main(String[] args) {
		CloudConfig.setTarget("server");
		
		double[] data = { 3, 4 };
		CloudSharedVar input = new CloudSharedVar("var123").init(data);
		input.storeToCloud();

		// This function will be sent to cloud
		CloudFunc func = new CloudFunc("func123", 
				new Expr[] { x, y }, sqrt(x*x + y*y));

		CloudSharedVar output = new CloudSharedVar("out123");
		// Evaluate the function on the cloud and 
		// return the reference of the result
		long begin = System.currentTimeMillis();
		for(int i=0; i<100; i++) {
			func.apply(output, input); 
		}
		long end = System.currentTimeMillis();
		System.out.println("Time: "+(end-begin)+"ms");
		
		System.out.println(output.getName());
		if(output.fetchToLocal()) {
			for (double d : output.getData())
				System.out.println(d);
		}
	}
}
