package io.lambdacloud.examples;

import static io.lambdacloud.symjava.symbolic.Symbol.C0;
import static io.lambdacloud.symjava.symbolic.Symbol.x;
import io.lambdacloud.core.CloudConfig;
import io.lambdacloud.core.CloudLib;
import io.lambdacloud.core.CloudSD;
import io.lambdacloud.symjava.examples.Newton;
import io.lambdacloud.symjava.relational.Eq;
import io.lambdacloud.symjava.symbolic.Expr;

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
		CloudConfig.setGlobalConfig("job_local.conf");
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
		CloudConfig.setGlobalConfig("job_local.conf");
		CloudLib lib = new CloudLib();
		
		double[] guess = new double[]{ 10 };
		//sever
		CloudSD output = new CloudSD("bs_sol").init(1);
		lib.solverNewton(new String[]{" eq(x^2-612, 0) "}, guess, 100, 1e-5, output);
		output.fetch();
		System.out.println(output.getData(0));
	}
}
