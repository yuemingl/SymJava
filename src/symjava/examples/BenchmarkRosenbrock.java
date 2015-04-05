package symjava.examples;

import static symjava.math.SymMath.pow;
import symjava.math.SymMath;
import symjava.matrix.SymMatrix;
import symjava.matrix.SymVector;
import symjava.numeric.NumMatrix;
import symjava.numeric.NumVector;
import symjava.symbolic.Expr;
import symjava.symbolic.Sum;
import symjava.symbolic.Symbol;
import symjava.symbolic.Symbols;

public class BenchmarkRosenbrock {

	public static void test() {
		int N = 300;
		long begin, end;
		Expr rosen = null;
		Symbol i = new Symbol("i");
		Symbols xi = new Symbols("x", i);
		Symbols xim1 = new Symbols("x", i-1);
		rosen  =  Sum.apply(100*pow(xi-xim1*xim1,2) + pow(1-xim1,2), i, 2, N);
		System.out.println("Rosenbrock function with N="+N+": "+rosen);
		
		boolean debug = false;
		
		Expr[] freeVars = xi.get(1, N);
		begin = System.currentTimeMillis();
		SymVector grad = SymMath.grad(rosen);
		SymMatrix hess = SymMath.hess(rosen);
		end = System.currentTimeMillis();
		System.out.println("Symbolic Time: "+((end-begin)/1000.0));
		
		begin = System.currentTimeMillis();
		NumVector numGrad = grad.toNumVector(freeVars);
		end = System.currentTimeMillis();
		System.out.println("Gradient Compile Time: "+((end-begin)/1000.0));

		begin = System.currentTimeMillis();
		NumMatrix numHess = hess.toNumMatrix(freeVars);
		end = System.currentTimeMillis();
		System.out.println("Hessian Compile Time: "+((end-begin)/1000.0));

		double[] outAry = null;
		
		double[] args = new double[freeVars.length];
		outAry = new double[N];
		double[] gradResult = numGrad.eval(outAry, args);
		if(debug) {
			System.out.println(grad);
			for(double d : gradResult)
				System.out.println(d);
			System.out.println();
		}
		
		outAry = new double[N*N];
		numHess.eval(outAry, args);
		double[][] hessResult = numHess.copyData();
		if(debug) {
			System.out.println(hess);
			for(double[] row : hessResult) {
				for(double d : row)
					System.out.print(d+" ");
				System.out.println();
			}
		}
		
		int NN = 1000000;
		begin = System.currentTimeMillis();
		for(int j=0; j<NN; j++)
			numGrad.eval(outAry, args);
		end = System.currentTimeMillis();
		System.out.println("Grad Evaluaton Time: "+((end-begin)/1000.0));
		
		begin = System.currentTimeMillis();
		for(int j=0; j<NN; j++)
			numHess.eval(outAry, args);
		end = System.currentTimeMillis();
		System.out.println("Hessian Evaluation Time: "+((end-begin)/1000.0));
		
	}
	public static void main(String[] args) {
		test();
	}
/*
Rosenbrock function with N=500: \Sigma_{i=2}^500{(1 - x_{-1 + i})^2 + 100*(x_i - (x_{-1 + i})^2)^2}
Symbolic Time: 4.837
Gradient Compile Time: 0.203
Hessian Compile Time: 0.11
Grad Evaluaton Time: 48.44
Hessian Evaluation Time: 45.009
 */
}
