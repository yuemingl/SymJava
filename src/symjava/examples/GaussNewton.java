package symjava.examples;

import Jama.Matrix;
import symjava.matrix.*;
import symjava.numeric.NumMatrix;
import symjava.numeric.NumVector;
import symjava.relational.Eq;
import symjava.symbolic.Expr;
import symjava.symbolic.utils.JIT;
import symjava.symbolic.utils.Utils;

/**
 * A general Gauss Newton solver using SymJava for simbolic computations
 * instead of writing your own Jacobian matrix and Residuals
 */
public class GaussNewton {

	public static double[] solve(Expr eq, double[] init, double[][] data, int maxIter, double eps) {
		return solve((Eq)eq, init, data, maxIter, eps);
	}
	public static double[] solve(Expr eq, double[] init, double[][] data) {
		return solve((Eq)eq, init, data, 30, 1e-8);
	}
	public static double[] solve(Expr eq, double[][] data) {
		Eq eq1 = (Eq)eq;
		Expr[] params = eq1.getParams();
		System.out.println("Model parameters: "+Utils.joinLabels(params, ","));
		return solve((Eq)eq, new double[params.length], data, 30, 1e-8);
	}
	
	public static double[] solve(Eq eq, double[] init, double[][] data, int maxIter, double eps) {
		int n = data.length;
		
		//Construct Jacobian Matrix and Residuals
		SymVector res = new SymVector(n);
		SymMatrix J = new SymMatrix(n, eq.getParams().length);
		
		Expr[] params = eq.getParams();
		for(int i=0; i<n; i++) {
			Eq subEq = eq.subsUnknowns(data[i]);
			res[i] = subEq.lhs() - subEq.rhs(); //res[i] =y[i] - f(x[i]);
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
		double[] outJac = new double[NJ.rowDim()*NJ.colDim()];
		double[] outRes = new double[Nres.dim()];
		for(int i=0; i<maxIter; i++) {
			//Use JAMA to solve the system
			NJ.eval(outJac, init);
			Matrix A = new Matrix(NJ.copyData());
			Matrix b = new Matrix(Nres.eval(outRes, init), Nres.dim());
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
		return init;
	}
}
