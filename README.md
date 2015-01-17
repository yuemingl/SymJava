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
Expr expr = x + y * z;
System.out.println(expr); //x + y*z

Expr expr2 = expr.subs(x, y*y);
System.out.println(expr2); //y^2 + y*z
System.out.println(expr2.diff(y)); //2*y + z


Func f = new Func("f1", expr2.diff(y));
System.out.println(f); //f1(y,z)


BytecodeFunc func = f.toBytecodeFunc();
System.out.println(func.apply(1,2)); //4.0
```

```Java
package symjava.examples;

import Jama.Matrix;
import symjava.matrix.*;
import symjava.relational.Eq;
import symjava.symbolic.Symbol;
import static symjava.symbolic.Symbol.*;

/**
 * Use Gauss-Newton algorithm to fit a given model y=a*x/(b-x)
 * See http://en.wikipedia.org/wiki/Gauss-Newton_algorithm
 *
 */
public class GaussNewton {

	public static void main(String[] args) {
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
		runGaussNewton(eq, initialGuess, data);

	}
	
	public static void runGaussNewton(Eq eq, double[] init, double[] ...data) {
		int n = data.length;
		
		//Construct Jacobian Matrix and Residuals
		SymVector res = new SymVector(n);
		SymMatrix J = new SymMatrix(n, eq.getParams().length);
		
		for(int i=0; i<n; i++) {
			Eq subEq = eq.subsUnknowns(data[i]);
			res[i] = subEq.lhs - subEq.rhs; //res[i] = y[i] - a*x[i]/(b + x[i]); 
			J[i][0] = res[i].diff(a);
			J[i][1] = res[i].diff(b);
		}
		
		System.out.println("Jacobian Matrix = ");
		J.print();
		System.out.println("Residuals = ");
		res.print();
		
		int maxIter = 10;
		double eps = 1e-4;
		Symbol[] params = {a, b};
		NumVector Nres = new NumVector(res, params);
		NumMatrix NJ = new NumMatrix(J, params);
		
		System.out.println("Iterativly sovle a and b in model y=a*x/(b-x) ... ");
		for(int i=0; i<maxIter; i++) {
			Matrix A = new Matrix(NJ.eval(init));
			Matrix b = new Matrix(Nres.eval(init), Nres.dim());
			Matrix x = A.solve(b); //Lease Square solution
			if(x.norm2() < eps) 
				break;
			//Update initial guess
			for(int j=0; j<init.length; j++) {
				init[j] = init[j] - x.get(j, 0);
				System.out.print(String.format("%s=%.3f",eq.getParams()[j], init[j])+" ");
			}
			System.out.println();
		}		
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
Iterativly sovle a and b in model y=a*x/(b-x) ... 
a=0.333 b=0.260 
a=0.343 b=0.426 
a=0.358 b=0.530 
a=0.361 b=0.554 
a=0.362 b=0.556 
a=0.362 b=0.556 
```

