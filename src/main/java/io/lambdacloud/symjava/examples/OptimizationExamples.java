package io.lambdacloud.symjava.examples;

import io.lambdacloud.symjava.relational.Eq;
import io.lambdacloud.symjava.relational.Ge;
import io.lambdacloud.symjava.symbolic.Expr;
import io.lambdacloud.symjava.symbolic.Sum;
import io.lambdacloud.symjava.symbolic.Symbol;
import io.lambdacloud.symjava.symbolic.Symbols;
import io.lambdacloud.symjava.symbolic.utils.AddList;
import static io.lambdacloud.symjava.math.SymMath.*;
import static io.lambdacloud.symjava.symbolic.Symbol.*;

/**
 * http://docs.scipy.org/doc/scipy/reference/tutorial/optimize.html
 * @author yliu
 *
 */
public class OptimizationExamples {

	public static void main(String[] args) {
		test1();
		test2();
		test3_constrained();
		test3_unconstrained();
		test3_OptSolver();
	}
	
	/**
	 * Newton-Conjugate-Gradient algorithm (method='Newton-CG')
	 */
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

	/**
	 * Newton-Conjugate-Gradient algorithm (method='Newton-CG')
	 */
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
	
	/**
	 * Constrained minimization of multivariate scalar functions (minimize)
	 *    min f(x,y)=2xy + 2x - x^2 - 2y^2
	 *    subject to
	 *      x^3 - y = 0
	 *      y-1 >= 0
	 */
	public static void test3_constrained() {
		Expr obj = 2*x*y + 2*x - x*x - 2*y*y;
		Symbol lmd1 = new Symbol("\\lambda_1");
		Symbol lmd2 = new Symbol("\\lambda_2");
		Symbol c = new Symbol("c");
		
		Expr L = obj + lmd1*(pow(x,3)-y) + lmd2*(y-1-c*c);
		Expr[] freeVars = new Expr[]{x, y, lmd1, lmd2, c};
		//double[] x0 = {-1.0,1.0, 0, 0, 0}; //stuck?
		double[] x0 = {0.0,0.0, 0, 0, 0}; //works
		Eq eq = new Eq(L, C0, freeVars);
		System.out.println(eq);

		NewtonOptimization.solve(eq, x0, 1000, 1e-6, false);
		//[ 1.00000009  1.        ]
	}

	/**
	 * Unconstrained minimization of multivariate scalar functions (minimize)
	 *    min f(x,y)=2xy + 2x - x^2 - 2y^2
	 */
	public static void test3_unconstrained() {
		Expr obj = 2*x*y + 2*x - x*x - 2*y*y;
		
		Expr L = obj;
		Expr[] freeVars = new Expr[]{x, y};
		double[] x0 = {-1.0,1.0};
		Eq eq = new Eq(L, C0, freeVars);
		System.out.println(eq);

		NewtonOptimization.solve(eq, x0, 1000, 1e-6, false);
		//[ 2.  1.]
	}

	/**
	 * Constrained minimization of multivariate scalar functions (minimize)
	 * with a wrapper class OptSolver
	 *    min f(x,y)=2xy + 2x - x^2 - 2y^2
	 *    subject to
	 *      x^3 - y = 0
	 *      y-1 >= 0
	 */
	public static void test3_OptSolver() {
		OptSolver.min(2*x*y + 2*x - x*x - 2*y*y)
			.subjectTo(
				Eq.apply(pow(x,3), y), //x^3-y =  0
				Ge.apply(y-1, 0)       //y-1   >= 0
			).solve();
	}
}
