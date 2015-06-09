package lambdacloud.examples;

import static symjava.math.SymMath.cos;
import static symjava.symbolic.Symbol.C0;
import static symjava.symbolic.Symbol.x;
import lambdacloud.core.CloudBreak;
import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudIf;
import lambdacloud.core.CloudInt;
import lambdacloud.core.CloudVar;
import lambdacloud.core.CloudLoop;
import lambdacloud.core.CSD;
import symjava.relational.Lt;
import symjava.symbolic.Expr;

public class TestCloudLoop {

	/**
	 * Test a simple loop 
	*/
	public static void testLoop1() {
		CloudInt i = new CloudInt("i");

		//for(int i=0; i<100; i++);
		CloudLoop loop = new CloudLoop(
				i.assign(0),      //i = 0
				Lt.apply(i, 100), //i < 100
				i.assign(i + 1)   //i = i+1
			);
		
		System.out.println(loop.compile(null).apply());
		loop.apply();
		
		//int i=0;
		//while(i<100) {
		// i++;
		//}
		CloudLoop loop2 = new CloudLoop(
				i.assign(0),     //i = 0
				Lt.apply(i, 100) //i < 100
			);
		loop2.appendBody(i.assign(i + 1)); // i=i+1
		System.out.println(loop2.compile(null).apply());
		loop2.apply();
	}
	
//	/**
//	 * Use Nnewton's method to solve x = cos(x) with
//	 * initial value x0=1.0
//	 * 
//	 * @param args
//	 */
//	public static void testLoop2() {
//		CloudConfig.setTarget("server");
//		
//		// Compute the derivative of f(x) locally
//		Expr f = x - cos(x);
//		Expr df = f.diff(x);
//		System.out.println("f(x)="+f+"    f.diff(x)="+df);
//		
//		// Define a cloud variable (global) to store 
//		//the initial value and solution after the iterations
//		CloudSharedVar x0 = new CloudSharedVar("x0");
//		x0.init(new double[] { 1.0 });
//		x0.storeToCloud();
//		
//		// Declare i as a local variable on the cloud task server
//		CloudVar i = new CloudVar("i");
//		C0.assignTo(i); //i=0;
//
//		/**
//		 *  Define a while loop on cloud:
//		 *  while(i<maxIter) {
//		 *    double dx = -f(x0)/df(x0);
//		 *    if(dx < 1e-5) break;
//		 *    x0 = x0 + dx;
//		 *  }
//		 */
//		int maxIter = 100;
//		CloudLoop loop = new CloudLoop( Lt.apply(i, maxIter) ); //while(i<maxIter)
//
//		// Declare a temporary local variable dx on the cloud task server
//		CloudVar dx = new CloudVar("dx");
//		//dx = -f/df;
//		loop.appendBody((-f/df).assignTo(dx));
//		
//		CloudIf stopCond = new CloudIf(Lt.apply(dx,1e-5)); //if(dx < 1e-5) break;
//		stopCond.appendTrue(new CloudBreak());
//		loop.appendBody(stopCond);
//		
//		// Update initial value x0 = x0 + dx
//		loop.appendBody((x0+dx).assignTo(x0));
//
//		// Now we have all our instructions, call apply() to run it on cloud
//		loop.apply(x0);
//		
//		// Get the result
//		x0.fetchToLocal();
//		System.out.println(x0.get(0));
//	}
	
	public static void main(String[] args) {
		testLoop1();
		//testLoop2();
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
