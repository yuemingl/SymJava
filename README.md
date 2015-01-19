# SymJava
SymJava is a Java library for symbolic mathematics.

There are two interesting features:

1. Operator Overloading is implemented by using https://github.com/amelentev/java-oo

2. Lambdify in sympy is implemented in SymJava by using BCEL library. The java Bytecode are generated in runtime for the expressions.

SymJava is developed under Java 7 and Eclipse-Kepler (SR2 4.3.2, https://www.eclipse.org/downloads/packages/release/kepler/sr2)

Install java-oo Eclipse plugin for Java Operator Overloading support (https://github.com/amelentev/java-oo):
Click in menu: Help -> Install New Software. Enter in "Work with" field: 
http://amelentev.github.io/eclipse.jdt-oo-site/

Examples:

```Java
package symjava.examples;

import static symjava.symbolic.Symbol.*;
import symjava.bytecode.BytecodeFunc;
import symjava.symbolic.*;

/**
 * This example uses Java Operator Overloading for symbolic computation. 
 * See https://github.com/amelentev/java-oo for Java Operator Overloading.
 * 
 */
public class Example1 {

	public static void main(String[] args) {
		Expr expr = x + y * z;
		System.out.println(expr); //x + y*z
		
		Expr expr2 = expr.subs(x, y*y);
		System.out.println(expr2); //y^2 + y*z
		System.out.println(expr2.diff(y)); //2*y + z
		
		Func f = new Func("f1", expr2.diff(y));
		System.out.println(f); //2*y + z
		
		BytecodeFunc func = f.toBytecodeFunc();
		System.out.println(func.apply(1,2)); //4.0
	}
}
```

```Java
package symjava.examples;

import symjava.relational.Eq;
import symjava.symbolic.Symbol;
import static symjava.symbolic.Symbol.*;


public class Example2 {

	/**
	 * Example from Wikipedia
	 * (http://en.wikipedia.org/wiki/Gauss-Newton_algorithm)
	 * 
	 * Use Gauss-Newton algorithm to fit a given model y=a*x/(b-x)
	 *
	 */
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
		
		//Here we go ...
		GaussNewton.solve(eq, initialGuess, data, 10, 1e-4);

	}
	
	/**
	 * Example from Apache Commons Math 
	 * (http://commons.apache.org/proper/commons-math/userguide/optimization.html)
	 * 
	 * "We are looking to find the best parameters [a, b, c] for the quadratic function 
	 * 
	 * f(x) = a x2 + b x + c. 
	 * 
	 * The data set below was generated using [a = 8, b = 10, c = 16]. A random number 
	 * between zero and one was added to each y value calculated. "
	 * 
	 */	
	public static void example2() {
		Symbol[] freeVars = {x};
		Symbol[] params = {a, b, c};
		Eq eq = new Eq(y, a*x*x + b*x + c, freeVars, params);
		
		double[][] data = {
				{1 , 34.234064369},
				{2 , 68.2681162306108},
				{3 , 118.615899084602},
				{4 , 184.138197238557},
				{5 , 266.599877916276},
				{6 , 364.147735251579},
				{7 , 478.019226091914},
				{8 , 608.140949270688},
				{9 , 754.598868667148},
				{10, 916.128818085883},		
		};
		
		double[] initialGuess = {1, 1, 1};
		
		GaussNewton.solve(eq, initialGuess, data, 10, 1e-4);
	}
	
	public static void main(String[] args) {
		example1();
		example2();
	}
}

```
Output:
```
Jacobian Matrix = 
-0.038/(0.038 + b)	0.038/(0.038 + b)^2*a	
-0.194/(0.194 + b)	0.194/(0.194 + b)^2*a	
-0.425/(0.425 + b)	0.425/(0.425 + b)^2*a	
-0.626/(0.626 + b)	0.626/(0.626 + b)^2*a	
-1.253/(1.253 + b)	1.253/(1.253 + b)^2*a	
-2.5/(2.5 + b)	2.5/(2.5 + b)^2*a	
-3.74/(3.74 + b)	3.74/(3.74 + b)^2*a	
Residuals = 
0.05 - 0.038/(0.038 + b)*a
0.127 - 0.194/(0.194 + b)*a
0.094 - 0.425/(0.425 + b)*a
0.2122 - 0.626/(0.626 + b)*a
0.2729 - 1.253/(1.253 + b)*a
0.2665 - 2.5/(2.5 + b)*a
0.3317 - 3.74/(3.74 + b)*a
Iterativly sovle ... 
a=0.33266 b=0.26017 
a=0.34281 b=0.42608 
a=0.35778 b=0.52951 
a=0.36141 b=0.55366 
a=0.36180 b=0.55607 
a=0.36183 b=0.55625 

Jacobian Matrix = 
-(1.0)^2	-1	-1	
-(2.0)^2	-2.0	-1	
-(3.0)^2	-3.0	-1	
-(4.0)^2	-4.0	-1	
-(5.0)^2	-5.0	-1	
-(6.0)^2	-6.0	-1	
-(7.0)^2	-7.0	-1	
-(8.0)^2	-8.0	-1	
-(9.0)^2	-9.0	-1	
-(10.0)^2	-10.0	-1	
Residuals = 
34.234064369 - (1.0)^2*a + b - c
68.2681162306108 - (2.0)^2*a + 2.0*b - c
118.615899084602 - (3.0)^2*a + 3.0*b - c
184.138197238557 - (4.0)^2*a + 4.0*b - c
266.599877916276 - (5.0)^2*a + 5.0*b - c
364.147735251579 - (6.0)^2*a + 6.0*b - c
478.019226091914 - (7.0)^2*a + 7.0*b - c
608.140949270688 - (8.0)^2*a + 8.0*b - c
754.598868667148 - (9.0)^2*a + 9.0*b - c
916.128818085883 - (10.0)^2*a + 10.0*b - c
Iterativly sovle ... 
a=7.99883 b=10.00184 c=16.32401 
```

```Java
package symjava.examples;

import Jama.Matrix;
import symjava.matrix.*;
import symjava.relational.Eq;

/**
 * A general Gauss Newton solver using SymJava for simbolic computations
 * instead of writing your own Jacobian matrix and Residuals
 */
public class GaussNewton {

	public static void solve(Eq eq, double[] init, double[][] data, int maxIter, double eps) {
		int n = data.length;
		
		//Construct Jacobian Matrix and Residuals
		SymVector res = new SymVector(n);
		SymMatrix J = new SymMatrix(n, eq.getParams().length);
		
		for(int i=0; i<n; i++) {
			Eq subEq = eq.subsUnknowns(data[i]);
			res[i] = subEq.lhs - subEq.rhs; //res[i] =y[i] - a*x[i]/(b + x[i]); 
			for(int j=0; j<eq.getParams().length; j++)
				J[i][j] = res[i].diff(eq.getParams()[j]);
		}
		
		System.out.println("Jacobian Matrix = ");
		J.print();
		System.out.println("Residuals = ");
		res.print();
		
		//Convert symbolic staff to Bytecode staff to speedup evaluation
		NumVector Nres = new NumVector(res, eq.getParams());
		NumMatrix NJ = new NumMatrix(J, eq.getParams());
		
		System.out.println("Iterativly sovle ... ");
		for(int i=0; i<maxIter; i++) {
			//Use JAMA to solve the system
			Matrix A = new Matrix(NJ.eval(init));
			Matrix b = new Matrix(Nres.eval(init), Nres.dim());
			Matrix x = A.solve(b); //Lease Square solution
			if(x.norm2() < eps) 
				break;
			//Update initial guess
			for(int j=0; j<init.length; j++) {
				init[j] = init[j] - x.get(j, 0);
				System.out.print(String.format("%s=%.5f",eq.getParams()[j], init[j])+" ");
			}
			System.out.println();
		}		
	}
}

```

```Java
package symjava.examples;

import static symjava.symbolic.Symbol.*;
import symjava.relational.Eq;
import symjava.symbolic.*;

public class Example3 {
	
	/**
	 * Square root of a number
	 * (http://en.wikipedia.org/wiki/Newton's_method)
	 */
	public static void example1() {
		Expr[] freeVars = {x};
		double num = 612;
		Eq[] eq = new Eq[] {
				new Eq(x*x-num, Symbol.C0, freeVars, null)
		};
		
		double[] guess = new double[]{ 10 };
		
		Newton.solve(eq, guess, 10,1e-3);
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
		
		NewtonOptimization.solve(lm.getEq(), lm.getInitialGuess(), 10, 1e-4);
	}
	
	public static void main(String[] args) {
		example1();
		example2();
	}
}
```

```
Jacobian Matrix = 
2*x	
Iterativly sovle ... 
x=10.00000 
x=35.60000 
x=26.39551 
x=24.79064 
x=24.73869 
Hessian Matrix = 
2	0	0	0	0	0	0	1	0	0	0	0	0	0	0	0	
0	2	0	0	0	0	0	0	1	0	0	0	0	0	0	0	
0	0	2	0	0	0	0	0	0	1	0	0	0	0	0	0	
0	0	0	2	0	0	0	0	0	0	1	0	0	0	0	0	
0	0	0	0	2	0	0	0	0	0	0	1	0	0	0	0	
0	0	0	0	0	2	0	0	0	0	0	0	1	0	0	0	
0	0	0	0	0	0	2	0	0	0	0	0	0	1	0	0	
1.0	0	0	0	0	0	0	0	0	0	0	0	0	0	-(0.001444*(b + 0.038)^2/(b + 0.038)^4 + 0.038*b*(b + 0.038)^2/(b + 0.038)^4)	-0.038*a*(b + 0.038)^2/(b + 0.038)^4 + 1.09744E-4*a/(b + 0.038)^4 + 0.076*a/(b + 0.038)^4*b^2 + 0.005776*a*b/(b + 0.038)^4	
0	1.0	0	0	0	0	0	0	0	0	0	0	0	0	-(0.037636*(b + 0.194)^2/(b + 0.194)^4 + 0.194*b*(b + 0.194)^2/(b + 0.194)^4)	-0.194*a*(b + 0.194)^2/(b + 0.194)^4 + 0.014602768000000002*a/(b + 0.194)^4 + 0.388*a/(b + 0.194)^4*b^2 + 0.150544*a*b/(b + 0.194)^4	
0	0	1.0	0	0	0	0	0	0	0	0	0	0	0	-(0.18062499999999998*(b + 0.425)^2/(b + 0.425)^4 + 0.425*b*(b + 0.425)^2/(b + 0.425)^4)	-0.425*a*(b + 0.425)^2/(b + 0.425)^4 + 0.15353124999999998*a/(b + 0.425)^4 + 0.85*a/(b + 0.425)^4*b^2 + 0.7224999999999999*a*b/(b + 0.425)^4	
0	0	0	1.0	0	0	0	0	0	0	0	0	0	0	-(0.391876*(b + 0.626)^2/(b + 0.626)^4 + 0.626*b*(b + 0.626)^2/(b + 0.626)^4)	-0.626*a*(b + 0.626)^2/(b + 0.626)^4 + 0.490628752*a/(b + 0.626)^4 + 1.252*a/(b + 0.626)^4*b^2 + 1.567504*a*b/(b + 0.626)^4	
0	0	0	0	1.0	0	0	0	0	0	0	0	0	0	-(1.5700089999999998*(b + 1.253)^2/(b + 1.253)^4 + 1.253*b*(b + 1.253)^2/(b + 1.253)^4)	-1.253*a*(b + 1.253)^2/(b + 1.253)^4 + 3.934442553999999*a/(b + 1.253)^4 + 2.506*a/(b + 1.253)^4*b^2 + 3.1400179999999995*a*b/(b + 1.253)^4 + 3.1400179999999995*a*b/(b + 1.253)^4	
0	0	0	0	0	1.0	0	0	0	0	0	0	0	0	-(6.25*(b + 2.5)^2/(b + 2.5)^4 + 2.5*b*(b + 2.5)^2/(b + 2.5)^4)	-2.5*a*(b + 2.5)^2/(b + 2.5)^4 + 31.25*a/(b + 2.5)^4 + 5.0*a*b^2/(b + 2.5)^4 + 25.0*a*b/(b + 2.5)^4	
0	0	0	0	0	0	1.0	0	0	0	0	0	0	0	-(13.987600000000002*(b + 3.74)^2/(b + 3.74)^4 + 3.74*b*(b + 3.74)^2/(b + 3.74)^4)	7.48*a*b^2/(b + 3.74)^4 - 3.74*a*(b + 3.74)^2/(b + 3.74)^4 + 104.62724800000002*a/(b + 3.74)^4 + 55.95040000000001*a*b/(b + 3.74)^4	
-0.0	-0.0	-0.0	-0.0	-0.0	-0.0	-0.0	-(0.001444*(b + 0.038)^2/(b + 0.038)^4 + 0.038*b*(b + 0.038)^2/(b + 0.038)^4)	-(0.037636*(b + 0.194)^2/(b + 0.194)^4 + 0.194*b*(b + 0.194)^2/(b + 0.194)^4)	-(0.18062499999999998*(b + 0.425)^2/(b + 0.425)^4 + 0.425*b*(b + 0.425)^2/(b + 0.425)^4)	-(0.391876*(b + 0.626)^2/(b + 0.626)^4 + 0.626*b*(b + 0.626)^2/(b + 0.626)^4)	-(1.5700089999999998*(b + 1.253)^2/(b + 1.253)^4 + 1.253*b*(b + 1.253)^2/(b + 1.253)^4)	-(6.25*(b + 2.5)^2/(b + 2.5)^4 + 2.5*b*(b + 2.5)^2/(b + 2.5)^4)	-(13.987600000000002*(b + 3.74)^2/(b + 3.74)^4 + 3.74*b*(b + 3.74)^2/(b + 3.74)^4)	-0.0	-(0.038*\lambda_0*(b + 0.038)^2/(b + 0.038)^4 - 1.09744E-4*\lambda_0/(b + 0.038)^4 - 0.076*\lambda_0/(b + 0.038)^4*b^2 + 0.002888*\lambda_0*b/(b + 0.038)^4 + 0.194*\lambda_1*(b + 0.194)^2/(b + 0.194)^4 - 0.388*\lambda_1/(b + 0.194)^4*b^2 - 0.7224999999999999*\lambda_2*b/(b + 0.425)^4 + 0.425*\lambda_2*(b + 0.425)^2/(b + 0.425)^4 - 0.85*\lambda_2/(b + 0.425)^4*b^2 - 1.252*\lambda_3/(b + 0.626)^4*b^2 + 1.567504*\lambda_3*b/(b + 0.626)^4 + 0.626*\lambda_3*(b + 0.626)^2/(b + 0.626)^4 - 0.490628752*\lambda_3/(b + 0.626)^4 - 2.506*\lambda_4/(b + 1.253)^4*b^2 + 6.280035999999999*\lambda_4*b/(b + 1.253)^4 + 1.253*\lambda_4*(b + 1.253)^2/(b + 1.253)^4 - 3.934442553999999*\lambda_4/(b + 1.253)^4 - 5.0*\lambda_5*b^2/(b + 2.5)^4 + 25.0*\lambda_5*b/(b + 2.5)^4 + 2.5*\lambda_5*(b + 2.5)^2/(b + 2.5)^4 - 31.25*\lambda_5/(b + 2.5)^4 - 7.48*\lambda_6*b^2/(b + 3.74)^4 + 104.62724800000002*\lambda_6/(b + 3.74)^4 + 3.74*\lambda_6*(b + 3.74)^2/(b + 3.74)^4 - 55.95040000000001*\lambda_6*b/(b + 3.74)^4 - 0.002888*\lambda_0*b/(b + 0.038)^4 + 0.014602768000000002*\lambda_1/(b + 0.194)^4 + 0.150544*\lambda_1*b/(b + 0.194)^4 + 0.15353124999999998*\lambda_2/(b + 0.425)^4)	
0	0	0	0	0	0	0	0.038*a*(b + 0.038)^2/(b + 0.038)^4	0.194*a*(b + 0.194)^2/(b + 0.194)^4	0.425*a*(b + 0.425)^2/(b + 0.425)^4	0.626*a*(b + 0.626)^2/(b + 0.626)^4	1.253*a*(b + 1.253)^2/(b + 1.253)^4	2.5*a*(b + 2.5)^2/(b + 2.5)^4	3.74*a*(b + 3.74)^2/(b + 3.74)^4	0.038*\lambda_0*(b + 0.038)^2/(b + 0.038)^4 + 0.194*\lambda_1*(b + 0.194)^2/(b + 0.194)^4 + 0.425*\lambda_2*(b + 0.425)^2/(b + 0.425)^4 + 0.626*\lambda_3*(b + 0.626)^2/(b + 0.626)^4 + 1.253*\lambda_4*(b + 1.253)^2/(b + 1.253)^4 + 2.5*\lambda_5*(b + 2.5)^2/(b + 2.5)^4 + 3.74*\lambda_6*(b + 3.74)^2/(b + 3.74)^4	-(27.975200000000005*\lambda_6*a/(b + 3.74)^4 + 7.48*\lambda_6*a*b/(b + 3.74)^4 + 0.002888*\lambda_0*a/(b + 0.038)^4 + 0.076*\lambda_0*a*b/(b + 0.038)^4 + 0.075272*\lambda_1*a/(b + 0.194)^4 + 0.388*\lambda_1*a*b/(b + 0.194)^4 + 0.36124999999999996*\lambda_2*a/(b + 0.425)^4 + 0.85*\lambda_2*a*b/(b + 0.425)^4 + 0.783752*\lambda_3*a/(b + 0.626)^4 + 1.252*\lambda_3*a*b/(b + 0.626)^4 + 3.1400179999999995*\lambda_4*a/(b + 1.253)^4 + 2.506*\lambda_4*a*b/(b + 1.253)^4 + 12.5*\lambda_5*a/(b + 2.5)^4 + 5.0*\lambda_5*a*b/(b + 2.5)^4)	
Grident = 
-0.1 + \lambda_0 + 2*y_0
-0.254 + \lambda_1 + 2*y_1
-0.188 + \lambda_2 + 2*y_2
-0.4244 + \lambda_3 + 2*y_3
-0.5458 + \lambda_4 + 2*y_4
-0.533 + \lambda_5 + 2*y_5
-0.6634 + \lambda_6 + 2*y_6
y_0 - 0.001444*a/(b + 0.038)^2 + 0.038*a*b/(b + 0.038)^2
y_1 - 0.037636*a/(b + 0.194)^2 + 0.194*a*b/(b + 0.194)^2
y_2 - 0.18062499999999998*a/(b + 0.425)^2 + 0.425*a*b/(b + 0.425)^2
y_3 - 0.391876*a/(b + 0.626)^2 + 0.626*a*b/(b + 0.626)^2
y_4 - 1.5700089999999998*a/(b + 1.253)^2 + 1.253*a*b/(b + 1.253)^2
y_5 - 6.25*a/(b + 2.5)^2 + 2.5*a*b/(b + 2.5)^2
y_6 - 13.987600000000002*a/(b + 3.74)^2 + 3.74*a*b/(b + 3.74)^2
-(13.987600000000002*\lambda_6/(b + 3.74)^2 + 3.74*\lambda_6*b/(b + 3.74)^2 + 0.001444*\lambda_0/(b + 0.038)^2 + 0.038*\lambda_0*b/(b + 0.038)^2 + 0.037636*\lambda_1/(b + 0.194)^2 + 0.194*\lambda_1*b/(b + 0.194)^2 + 0.18062499999999998*\lambda_2/(b + 0.425)^2 + 0.425*\lambda_2*b/(b + 0.425)^2 + 0.391876*\lambda_3/(b + 0.626)^2 + 0.626*\lambda_3*b/(b + 0.626)^2 + 1.5700089999999998*\lambda_4/(b + 1.253)^2 + 1.253*\lambda_4*b/(b + 1.253)^2 + 6.25*\lambda_5/(b + 2.5)^2 + 2.5*\lambda_5*b/(b + 2.5)^2)
0.038*\lambda_0*a/(b + 0.038)^2 + 0.194*\lambda_1*a/(b + 0.194)^2 + 0.425*\lambda_2*a/(b + 0.425)^2 + 0.626*\lambda_3*a/(b + 0.626)^2 + 1.253*\lambda_4*a/(b + 1.253)^2 + 2.5*\lambda_5*a/(b + 2.5)^2 + 3.74*\lambda_6*a/(b + 3.74)^2
Iterativly sovle ... 
y_0=0.00000 y_1=0.00000 y_2=0.00000 y_3=0.00000 y_4=0.00000 y_5=0.00000 y_6=0.00000 \lambda_0=0.00000 \lambda_1=0.00000 \lambda_2=0.00000 \lambda_3=0.00000 \lambda_4=0.00000 \lambda_5=0.00000 \lambda_6=0.00000 a=0.90000 b=0.20000 
y_0=0.01678 y_1=0.09612 y_2=0.16729 y_3=0.20243 y_4=0.25473 y_5=0.28945 y_6=0.30273 \lambda_0=0.06643 \lambda_1=0.06176 \lambda_2=-0.14658 \lambda_3=0.01955 \lambda_4=0.03634 \lambda_5=-0.04590 \lambda_6=0.05794 a=0.33266 b=0.26017 
y_0=0.01624 y_1=0.08735 y_2=0.15765 y_3=0.19518 y_4=0.25469 y_5=0.29667 y_6=0.31327 \lambda_0=0.06752 \lambda_1=0.07930 \lambda_2=-0.12729 \lambda_3=0.03404 \lambda_4=0.03642 \lambda_5=-0.06034 \lambda_6=0.03687 a=0.35178 b=0.46125 
y_0=0.02256 y_1=0.09240 y_2=0.15593 y_3=0.19116 y_4=0.25076 y_5=0.29644 y_6=0.31550 \lambda_0=0.05487 \lambda_1=0.06919 \lambda_2=-0.12387 \lambda_3=0.04207 \lambda_4=0.04428 \lambda_5=-0.05989 \lambda_6=0.03240 a=0.36223 b=0.55462 
y_0=0.02314 y_1=0.09356 y_2=0.15671 y_3=0.19159 y_4=0.25059 y_5=0.29598 y_6=0.31499 \lambda_0=0.05373 \lambda_1=0.06689 \lambda_2=-0.12542 \lambda_3=0.04123 \lambda_4=0.04463 \lambda_5=-0.05896 \lambda_6=0.03342 a=0.36185 b=0.55631 

```
