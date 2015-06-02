package lambdacloud.examples;

import symjava.relational.Lt;
import symjava.symbolic.Expr;
import symjava.symbolic.Symbol;
import lambdacloud.core.CloudBreak;
import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudIf;
import lambdacloud.core.CloudLocalVar;
import lambdacloud.core.CloudLoop;
import lambdacloud.core.CloudVar;
import static symjava.math.SymMath.*;
import static symjava.symbolic.Symbol.*;

public class TestCloudLoop {

	public static void testLoop1() {
		/**
		 * This test implements a loop to accumulate i from 0 to 99. 
		 * This is equivalent to 
			for(int i=0; i<100; i++);
			or equivalent to
			int i=0;
			while(i<100) {
			 i++;
			}
		 */
		
		CloudLocalVar i = new CloudLocalVar("i");
		C0.assignTo(i); //i=0
		CloudLoop loop = new CloudLoop( Lt.apply(i, 100) ); // while(i<100)
		loop.addBodyExpr((i + 1).assignTo(i)); // i=i+1
		loop.apply();
		
	}
	
	/**
	 * Use Nnewton's method to solve x = cos(x) with
	 * initial value x0=1.0
	 * 
	 * @param args
	 */
	public static void testLoop2() {
		CloudConfig.setTarget("server");
		
		// Compute the derivative of f(x) locally
		Expr f = x - cos(x);
		Expr df = f.diff(x);
		System.out.println("f(x)="+f+"    f.diff(x)="+df);
		
		// Define a cloud variable (global) to store 
		//the initial value and solution after the iterations
		CloudVar x0 = new CloudVar("x0");
		x0.init(new double[] { 1.0 });
		x0.storeToCloud();
		
		// Declare i as a local variable on the cloud task server
		CloudLocalVar i = new CloudLocalVar("i");
		C0.assignTo(i); //i=0;

		/**
		 *  Define a while loop on cloud:
		 *  while(i<maxIter) {
		 *    double dx = -f(x0)/df(x0);
		 *    if(dx < 1e-5) break;
		 *    x0 = x0 + dx;
		 *  }
		 */
		int maxIter = 100;
		CloudLoop loop = new CloudLoop( Lt.apply(i, maxIter) ); //while(i<maxIter)

		// Declare a temporary local variable dx on the cloud task server
		CloudLocalVar dx = new CloudLocalVar("dx");
		//dx = -f/df;
		loop.addBodyExpr((-f/df).assignTo(dx));
		
		CloudIf stopCond = new CloudIf(Lt.apply(dx,1e-5)); //if(dx < 1e-5) break;
		stopCond.addTrueBranch(new CloudBreak(), null);
		loop.addBodyExpr(stopCond);
		
		// Update initial value x0 = x0 + dx
		loop.addBodyExpr((x0+dx).assignTo(x0));

		// Now we have all our instructions, call apply() to run it on cloud
		loop.apply(x0);
		
		// Get the result
		x0.fetchToLocal();
		System.out.println(x0.get(0));
	}
	
	public static void main(String[] args) {
		testLoop1();
		testLoop2();
	}
}
/**
 * \begin{displaymath}\begin{array}{cl} 
 * x_1=&1.\\ 
 * x_2=&0.7503638678402438930349423... ...7673873\\ 
 * x_8=&0.739085133215160641655312087673873 \end{array}
 * \end{displaymath}
 * 
 */