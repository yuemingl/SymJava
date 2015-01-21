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
		GaussNewton.solve(eq, initialGuess, data, 100, 1e-4);

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
		
		GaussNewton.solve(eq, initialGuess, data, 100, 1e-4);
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
-0.038/(b + 0.038)	0.038*a*(b + 0.038)^-2	
-0.194/(b + 0.194)	0.194*a*(b + 0.194)^-2	
-0.425/(b + 0.425)	0.425*a*(b + 0.425)^-2	
-0.626/(b + 0.626)	0.626*a*(b + 0.626)^-2	
-1.253/(b + 1.253)	1.253*a*(b + 1.253)^-2	
-2.5/(b + 2.5)	2.5*a*(b + 2.5)^-2	
-3.74/(b + 3.74)	3.74*a*(b + 3.74)^-2	
Residuals = 
0.05 - 0.038*a/(b + 0.038)
0.127 - 0.194*a/(b + 0.194)
0.094 - 0.425*a/(b + 0.425)
0.2122 - 0.626*a/(b + 0.626)
0.2729 - 1.253*a/(b + 1.253)
0.2665 - 2.5*a/(b + 2.5)
0.3317 - 3.74*a/(b + 3.74)
Iterativly sovle ... 
a=0.33266 b=0.26017 
a=0.34281 b=0.42608 
a=0.35778 b=0.52951 
a=0.36141 b=0.55366 
a=0.36180 b=0.55607 
a=0.36183 b=0.55625 
Jacobian Matrix = 
-1	-1	-1	
-4.0	-2.0	-1.0	
-9.0	-3.0	-1.0	
-16.0	-4.0	-1.0	
-25.0	-5.0	-1.0	
-36.0	-6.0	-1.0	
-49.0	-7.0	-1.0	
-64.0	-8.0	-1.0	
-81.0	-9.0	-1.0	
-100.0	-10.0	-1.0	
Residuals = 
34.234064369 - c + a + b
68.2681162306108 - c + 4.0*a + 2.0*b
118.615899084602 - c + 9.0*a + 3.0*b
184.138197238557 - c + 16.0*a + 4.0*b
266.599877916276 - c + 25.0*a + 5.0*b
364.147735251579 - c + 36.0*a + 6.0*b
478.019226091914 - c + 49.0*a + 7.0*b
608.140949270688 - c + 64.0*a + 8.0*b
754.598868667148 - c + 81.0*a + 9.0*b
916.128818085883 - c + 100.0*a + 10.0*b
Iterativly sovle ... 
a=7.99883 b=10.00184 c=16.32401 
```

```Java
package symjava.examples;

import Jama.Matrix;
import symjava.matrix.*;
import symjava.relational.Eq;
import symjava.symbolic.Expr;

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
		
		Expr[] params = eq.getParams();
		for(int i=0; i<n; i++) {
			Eq subEq = eq.subsUnknowns(data[i]);
			res[i] = subEq.lhs - subEq.rhs; //res[i] =y[i] - a*x[i]/(b + x[i]); 
			for(int j=0; j<eq.getParams().length; j++) {
				Expr df = res[i].diff(params[j]);
				J[i][j] = df;
			}
		}
		
		System.out.println("Jacobian Matrix = ");
		System.out.println(J);
		System.out.println("Residuals = ");
		System.out.println(res);
		
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
		
		NewtonOptimization.solve(lm.getEq(), lm.getInitialGuess(), 100, 1e-4);
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
```
![](https://github.com/yuemingl/SymJava/blob/master/images/ex3_L.png)
![](https://github.com/yuemingl/SymJava/blob/master/images/ex3_hessian.png)
![](https://github.com/yuemingl/SymJava/blob/master/images/ex3_grad.png)

```Java
package symjava.examples;

import static symjava.symbolic.Symbol.*;
import symjava.matrix.*;
import symjava.symbolic.*;

/**
 * Example for PDE Constrained Parameters Optimization
 *
 */
public class Example4 {
	public static void main(String[] args) {
		Func u =  new Func("u",  x,y,z);
		Func u0 = new Func("u0", x,y,z);
		Func q =  new Func("q",  x,y,z);
		Func q0 = new Func("q0", x,y,z);
		Func f =  new Func("f",  x,y,z);
		Func lamd = new Func("\\lambda ", x,y,z);
		
		Expr reg_term = (q-q0)*(q-q0)*0.5*0.1;

		Func L = new Func("L",(u-u0)*(u-u0)/2 + reg_term + q*Dot.apply(Grad.apply(u), Grad.apply(lamd)) - f*lamd);
		System.out.println("Lagrange L(u, \\lambda, q) = \n"+L);
		
		Func phi = new Func("\\phi ", x,y,z);
		Func psi = new Func("\\psi ", x,y,z);
		Func chi = new Func("\\chi ", x,y,z);
		Expr[] xs =  new Expr[]{u,   lamd, q   };
		Expr[] dxs = new Expr[]{phi, psi,  chi };
		SymVector Lx = Grad.apply(L, xs, dxs);
		System.out.println("\nGradient Lx = (Lu, Llamd, Lq) =");
		System.out.println(Lx);
		
		Func du = new Func("\\delta{u}", x,y,z);
		Func dl = new Func("\\delta{\\lambda}", x,y,z);
		Func dq = new Func("\\delta{q}", x,y,z);
		Expr[] dxs2 = new Expr[] { du, dl, dq };
		SymMatrix Lxx = new SymMatrix();
		for(Expr Lxi : Lx) {
			Lxx.add(Grad.apply(Lxi, xs, dxs2));
		}
		System.out.println("\nHessian Matrix =");
		System.out.println(Lxx);
	}
}
```
![](https://github.com/yuemingl/SymJava/blob/master/images/ex4_L.png)
![](https://github.com/yuemingl/SymJava/blob/master/images/ex4_hessian.png)
![](https://github.com/yuemingl/SymJava/blob/master/images/ex4_grad.png)
