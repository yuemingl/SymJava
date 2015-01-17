# SymJava
SymJava is a Java library for symbolic mathematics.

There are two important features:

1. Operator Overloading is implemented by using https://github.com/amelentev/java-oo

2. Lambdify in sympy is implemented in SymJava by using BCEL library

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
		System.out.println(expr);
		
		Expr expr2 = expr.subs(x, y*y);
		System.out.println(expr2);
		System.out.println(expr2.diff(y));
		
		Func f = new Func("f1", expr2.diff(y));
		System.out.println(f);
		
		BytecodeFunc func = f.toBytecodeFunc();
		System.out.println(func.apply(1,2));
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
		Symbol[] unknowns = {x, y};
		Symbol[] params = {a, b};
		Eq eq = new Eq(y, a*x/(b+x), unknowns, params); 
		
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
		Symbol[] unknowns = {x, y};
		Symbol[] params = {a, b, c};
		Eq eq = new Eq(y, a*x*x + b*x + c, unknowns, params);
		
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
		
		double[] initialSolution = {1, 1, 1};
		
		GaussNewton.solve(eq, initialSolution, data, 10, 1e-4);
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
