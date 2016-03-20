package lambdacloud.examples;

import static symjava.symbolic.Symbol.a;
import static symjava.symbolic.Symbol.b;
import static symjava.symbolic.Symbol.x;
import static symjava.symbolic.Symbol.y;
import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudLib;
import symjava.examples.GaussNewton;
import symjava.relational.Eq;
import symjava.symbolic.Symbol;

/**
 * Example from Wikipedia
 * (http://en.wikipedia.org/wiki/Gauss-Newton_algorithm)
 * 
 * Use Gauss-Newton algorithm to fit a given model y=a*x/(b-x)
 *
 */
public class ExampleModelFit {

	public static void example1() {
		//Model y=a*x/(b-x), Unknown parameters: a, b
		Symbol[] freeVars = {x};
		Symbol[] params = {a, b};
		Eq eq = new Eq(y, a*x/(b+x), freeVars, params); 
		
		//Data for (x,y)
		double[][] data = {
			{0.038,0.050},
			{0.194,0.127},
			{0.425,0.094},
			{0.626,0.2122},
			{1.253,0.2729},
			{2.500,0.2665},
			{3.740,0.3317}
		};
		
		double[] initialGuess = {0.9, 0.2};
		
		//Local solver
		GaussNewton.solve(eq, initialGuess, data, 100, 1e-4);
		
		
		CloudConfig.setGlobalConfig("job_local.conf");
		CloudLib lib = new CloudLib();
		
		//Symbolic representation of the equation
		double[] rlt = lib.solverGaussNewton(eq, initialGuess, data, 100, 1e-4);
		for(double d : rlt)
			System.out.println(d);
		
		//Pass the string representation of the equation
		rlt = lib.solverGaussNewton("eq( y,a/(b + x)*x, array(x), array(a,b) )", initialGuess, data, 100, 1e-4);
		for(double d : rlt)
			System.out.println(d);
	}
	
	public static void main(String[] args) {
		example1();
	}

}
