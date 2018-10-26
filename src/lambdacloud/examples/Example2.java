package lambdacloud.examples;

import static symjava.math.SymMath.sqrt;
import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudSD;
import symjava.symbolic.Expr;
import static symjava.symbolic.Symbol.x;
import static symjava.symbolic.Symbol.y;


/**
 * In this example, we define a function on the fly.
 * 
 * double fun(double x, double y) {
 * 	return Math.sqrt(x*x + y*y);
 * }
 * 
 * It will be pushed to the cloud after initializing of the instance.
 * 
 * A cloud shared data 'input' is defined to be used as 
 * the parameter of the function. 
 * 
 * The newly defined function is called from local machine and
 * the return value is stored on the cloud named as 'output'.
 * We fetch the data 'output' and print it. 
 * 
 */
public class Example2 {

	public static void main(String[] args) {
		CloudConfig.setGlobalConfig("job_local.conf");
		
		Expr expr = sqrt(x*x + y*y);
		CloudFunc f = new CloudFunc(expr, new Expr[]{x, y});
		
		CloudSD input = new CloudSD("input").init(new double[]{3, 4});
		
		CloudSD output = new CloudSD("output");
		f.apply(output, input);
		//You have to fetch the result to local after evaluating, 
		//since it's stored on cloud by default
		if(output.fetch()) {
			for(double d : output.getData()) {
				System.out.println(d);
			}
		}
	}
}
