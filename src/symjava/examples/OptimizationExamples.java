package symjava.examples;

import symjava.relational.Eq;
import symjava.symbolic.Expr;
import symjava.symbolic.Sum;
import symjava.symbolic.Symbol;
import symjava.symbolic.Symbols;
import symjava.symbolic.utils.AddList;
import static symjava.math.SymMath.*;
import static symjava.symbolic.Symbol.C0;

public class OptimizationExamples {

	public static void main(String[] args) {
		test1();
		test2();
	}
	
	public static void test1() {
		//Rosenbrock function of N variables
		int N = 5;
		Expr rosen = null;
		Symbols xs = new Symbols("x");
		AddList addList = new AddList();
		for(int i=2; i<=N; i++) {
			addList.add( 100*pow(xs[i]-xs[i-1]*xs[i-1],2) + pow(1-xs[i-1],2) );
		}
		rosen = addList.toExpr().simplify();
		System.out.println(rosen);

		Expr[] freeVars = xs.get(1, N);
		double[] x0 = {1.3, 0.7, 0.8, 1.9, 1.2};
		Eq eq = new Eq(rosen, C0, freeVars);
		System.out.println(eq);

		NewtonOptimization.solve(eq, x0, 1000, 1e-6, false);
		//[ 1.  1.  1.  1.  1.]
	}

	public static void test2() {
		//Another way for the definition of Rosenbrock function
		int N = 5;
		Expr rosen = null;
		Symbol i = new Symbol("i");
		Symbols xi = new Symbols("x", i);
		Symbols xim1 = new Symbols("x", i-1);
		rosen  =  Sum.apply(100*pow(xi-xim1*xim1,2) + pow(1-xim1,2), i, 2, N);
		System.out.println(rosen);
		
		Expr[] freeVars = xi.get(1, N);
		double[] x0 = {1.3, 0.7, 0.8, 1.9, 1.2};
		Eq eq = new Eq(rosen, C0, freeVars);
		System.out.println(eq);

		NewtonOptimization.solve(eq, x0, 1000, 1e-6, false);
		//[ 1.  1.  1.  1.  1.]
	}

}
