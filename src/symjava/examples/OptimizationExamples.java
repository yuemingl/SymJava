package symjava.examples;

import symjava.relational.Eq;
import symjava.symbolic.Expr;
import symjava.symbolic.Sum;
import symjava.symbolic.Symbols;
import symjava.symbolic.utils.AddList;
import static symjava.math.SymMath.*;
import static symjava.symbolic.Symbol.C0;

public class OptimizationExamples {

	public static void main(String[] args) {
		test1();
	}
	
	public static void test1() {
		//Rosenbrock function of N variables
		Symbols x = new Symbols("x");
		AddList sum = new AddList();
		int N = 5;
		for(int i=2; i<=N; i++) {
			sum.add( 100*pow(x[i]-x[i-1]*x[i-1],2) + pow(1-x[i-1],2) );
		}
		double[] x0 = {1.3, 0.7, 0.8, 1.9, 1.2};
		
		Expr[] freeVars = x.getRange(1, N);
		Eq eq = new Eq(sum.toExpr(), C0, freeVars);
		System.out.println(eq);

		NewtonOptimization.solve(eq, x0, 1000, 1e-6, false);
		//[ 1.  1.  1.  1.  1.]
		
	}

}
