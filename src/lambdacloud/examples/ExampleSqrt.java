package lambdacloud.examples;

import static symjava.symbolic.Symbol.C0;
import static symjava.symbolic.Symbol.x;
import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudLib;
import lambdacloud.core.CloudSD;
import symjava.examples.Newton;
import symjava.relational.Eq;
import symjava.symbolic.Expr;

/**
 * Square root of a number
 * (http://en.wikipedia.org/wiki/Newton's_method)
 */
public class ExampleSqrt {

	public static void main(String[] args) {
		example1();
		example2();
	}
	public static void example1() {
		CloudConfig.setGlobalTarget("job_local.conf");
		CloudLib lib = new CloudLib();
		Expr[] freeVars = {x};
		double num = 612;
		Eq[] eq = new Eq[] {
				new Eq(x*x-num, C0, freeVars)
		};
		
		double[] guess = new double[]{ 10 };
		
		Newton.solve(eq, guess, 100, 1e-3);
		//sever
		CloudSD output = new CloudSD("bs_sol").init(1);
		lib.solverNewton(eq, guess, 100, 1e-5, output);
		output.fetch();
		System.out.println(output.getData(0));
	}
	
	public static void example2() {
		CloudConfig.setGlobalTarget("job_local.conf");
		CloudLib lib = new CloudLib();
		
		double[] guess = new double[]{ 10 };
		//sever
		CloudSD output = new CloudSD("bs_sol").init(1);
		lib.solverNewton(new String[]{" eq(x^2-612, 0) "}, guess, 100, 1e-5, output);
		output.fetch();
		System.out.println(output.getData(0));
	}
}
