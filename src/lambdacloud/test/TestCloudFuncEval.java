package lambdacloud.test;

import static symjava.math.SymMath.sqrt;
import static symjava.symbolic.Symbol.x;
import static symjava.symbolic.Symbol.y;
import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudSD;

public class TestCloudFuncEval {

	public static void main(String[] args) {
		CloudConfig.setGlobalTarget("job_local.conf");
		
		double[] data = { 3, 4 };
		CloudSD input = new CloudSD("var123").init(data);
		//input.push(); // The input will be pushed to cloud automatically when evaluating a function

		// This function will be sent to cloud
		CloudFunc func = new CloudFunc(sqrt(x*x + y*y));

		CloudSD output = new CloudSD();
		// Evaluate the function on the cloud and 
		// return the reference of the result
		long begin = System.currentTimeMillis();
		for(int i=0; i<100; i++) {
			func.apply(output, input); 
		}
		long end = System.currentTimeMillis();
		System.out.println("Time: "+(end-begin)+"ms");
		
		System.out.println(output.getName());
		if(output.fetch()) {
			for (double d : output.getData())
				System.out.println(d);
		}
	}
}
