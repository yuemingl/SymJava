package io.lambdacloud.symjava.examples;

import static io.lambdacloud.symjava.symbolic.Symbol.*;
import io.lambdacloud.symjava.relational.Eq;
import io.lambdacloud.symjava.symbolic.*;

public class Example3 {
	
	/**
	 * Square root of a number
	 * (http://en.wikipedia.org/wiki/Newton's_method)
	 */
	public static void example1() {
		Expr[] freeVars = {x};
		double num = 612;
		Eq[] eq = new Eq[] {
				new Eq(x*x-num, C0, freeVars)
		};
		
		double[] guess = new double[]{ 10 };
		
		Newton.solve(eq, guess, 100, 1e-3);
	}
	
	/**
	 * Example from Wikipedia
	 * (http://en.wikipedia.org/wiki/Gauss-Newton_algorithm)
	 * 
	 * Use Lagrange Multipliers and Newton method to fit a given model y=a*x/(b-x)
	 *
	 */
	public static void example2() {
		//Model y=a*x/(b-x), Unknown parameters: a, b
		Symbol[] freeVars = {x};
		Symbol[] params = {a, b};
		Eq eq = new Eq(y - a*x/(b+x), C0, freeVars, params); 
		
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
		
		LagrangeMultipliers lm = new LagrangeMultipliers(eq, initialGuess, data);
		//Just for purpose of displaying summation expression
		Eq L = lm.getEqForDisplay(); 
		System.out.println("L("+SymPrinting.join(L.getUnknowns(),",")+")=\n    "+L.lhs());
		System.out.println("where data array is (X_i, Y_i), i=0..."+(data.length-1));
		
		NewtonOptimization.solve(L, lm.getInitialGuess(), 100, 1e-4, true);
		
		Eq L2 = lm.getEq();
		System.out.println("L("+SymPrinting.join(L.getUnknowns(),",")+")=\n    "+L2.lhs());
		NewtonOptimization.solve(L2, lm.getInitialGuess(), 100, 1e-4, false);
		//\lambda_6=0.03343 a=0.36184 b=0.55624 
	}
	
	public static void main(String[] args) {
		example1();
		example2();
	}
}
