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
	
	/**
	 * A general Gauss Newton solver
	 * @param eq
	 * @param init
	 * @param data
	 */
	public static void runGaussNewton(Eq eq, double[] init, double[] ...data) {
		int n = data.length;
		
		//Construct Jacobian Matrix and Residuals
		SymVector res = new SymVector(n);
		SymMatrix J = new SymMatrix(n, eq.getParams().length);
		
		for(int i=0; i<n; i++) {
			Eq subEq = eq.subsUnknowns(data[i]);
			res[i] = subEq.lhs - subEq.rhs; //res[i] =y[i] - a*x[i]/(b + x[i]); 
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
		//Convert symbolic staff to Bytecode staff to speedup evaluation
		NumVector Nres = new NumVector(res, params);
		NumMatrix NJ = new NumMatrix(J, params);
		
		System.out.println("Iterativly sovle a and b in model y=a*x/(b-x) ... ");
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
				System.out.print(String.format("%s=%.3f",eq.getParams()[j], init[j])+" ");
			}
			System.out.println();
		}		
	}
}
