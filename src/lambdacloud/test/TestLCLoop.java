package lambdacloud.test;

import static symjava.math.SymMath.cos;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudSD;
import lambdacloud.core.lang.LCBuilder;
import lambdacloud.core.lang.LCInt;
import lambdacloud.core.lang.LCLoop;
import lambdacloud.core.lang.LCReturn;
import lambdacloud.core.lang.LCStatements;
import lambdacloud.core.lang.LCVar;
import symjava.math.SymMath;
import symjava.relational.Lt;
import symjava.symbolic.Expr;

public class TestLCLoop {

	/**
	 * Test a simple loop 
	*/
	public static void testLoop1() {
		LCInt i = new LCInt("i");
		LCVar x = LCVar.getDouble("x");

		LCStatements lcs1 = new LCStatements();
		//for(int i=0; i<100; i++);
		LCLoop loop = new LCLoop(
				i.assign(0),      //i = 0
				Lt.apply(i, 100), //i < 100
				i.assign(i + 1)   //i = i+1
			);
		lcs1.append(loop);
		lcs1.append(new LCReturn(x));
		System.out.println(lcs1);
		System.out.println(CompileUtils.compile(lcs1).apply());
		
		//int i=0;
		//while(i<100) {
		// i++;
		//}
		LCStatements lcs2 = new LCStatements();
		LCLoop loop2 = new LCLoop(
				i.assign(0),     //i = 0
				Lt.apply(i, 100) //i < 100
			);
		loop2.appendBody(i.assign(i + 1)); // i=i+1
		lcs2.append(loop2);
		lcs2.append(new LCReturn(x));
		System.out.println(lcs2);
		System.out.println(CompileUtils.compile(lcs2).apply());
	}
	
	/**
	 * Use Newton's method to solve x = cos(x) with initial value x0=1.0
	 * (http://www.sosmath.com/calculus/diff/der07/der07.html)
	 * 
	 * The following function is generated for the cloud task server.
	 *  double apply(double x) {
	 *    for(int i=0; i<100; i++) {
	 *      double dx = -f(x)/df(x); //Symbolic expression
	 *      if(abs(dx) < 1e-5) break;
	 *      x = x + dx;
	 *    }
	 *    return x;
	 *  }
	 *  
	 * Results:
	 * x_1=1.
	 * x_2=0.750363867840
	 * ...
	 * x_8=0.739085133215
	 */
	public static void testLoop2() {
		LCBuilder lcb = new LCBuilder("server");
		
		// Compute the derivative of f(x) locally
		LCVar x = LCVar.getDouble("x");
		Expr f = x - cos(x);
		Expr df = f.diff(x);
		System.out.println("f(x)="+f+"    f.diff(x)="+df);
		
		int maxIter = 100;
		LCInt i = new LCInt("i");                           //int i = 0;
		LCVar dx = LCVar.getDouble("dx");                   //double dx = 0;
		
		lcb.For(i.assign(0), Lt.apply(i, maxIter), i.inc()) // for(int i=0; i<100; i++) {
			.appendBody( dx.assign(-f/df) )                 //   dx = -f(x)/df(x);
			.breakIf(Lt.apply(SymMath.abs(dx), 1e-5))       //   if(dx < 1e-5) break;
			.appendBody(x.assign(x + dx))                   //   x = x + dx;
			;                                               // }
		lcb.Return(x);                                      // return x;
		System.out.println(lcb);

		// Build function: double apply(double x)
		CloudFunc func = lcb.build(x);
		
		// Define the cloud shared variables to store the initial value and solution
		CloudSD x0 = new CloudSD("x0");
		x0.init(new double[] { 1.0 });
		x0.push();
		CloudSD ans = new CloudSD("ans").resize(1);

		func.apply(ans, x0); // Run the function on the cloud task server
		
		// Get the result. (=0.7390851)
		ans.fetch();
		System.out.println("Solution: "+ans.getData(0));
		System.out.println("Verify: "+applyTest(1.0));
	}
	
	/**
	 * Verify the solution locally
	 * @param x
	 * @return
	 */
	public static double applyTest(double x) {
		int i = 0;
		for(; i < 100; i++) {
		    double dx = -(-Math.cos(x) + x)/(1 + Math.sin(x));
		    if( Math.abs(dx) < 1.0E-5 ) {
		        break;
		    }
		    x = dx + x;
		}
		System.out.println("Max iter="+i);
		return x;
	}
	
	public static void main(String[] args) {
		testLoop1();
		testLoop2();
	}
}

