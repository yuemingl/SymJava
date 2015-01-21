package symjava.examples;

import symjava.matrix.NumMatrix;
import symjava.matrix.NumVector;
import symjava.matrix.SymMatrix;
import symjava.matrix.SymVector;
import symjava.relational.Eq;
import symjava.symbolic.Expr;
import symjava.symbolic.Symbol;
import Jama.Matrix;

public class NewtonOptimization {
	public static void solve(Eq eq, double[] init, int maxIter, double eps) {
		if(!Symbol.C0.symEquals(eq.rhs)) {
			System.out.println("The right hand side of the equation must be 0.");
			return;
		}
		Expr[] unknowns = eq.getUnknowns();
		int n = unknowns.length;
		
		//Construct Hessian Matrix
		SymVector grad = new SymVector(n);
		SymMatrix hess = new SymMatrix(n, n);
		Expr L = eq.lhs;
		for(int i=0; i<n; i++) {
			grad[i] = L.diff(unknowns[i]);
			for(int j=0; j<n; j++) {
				Expr df = grad[i].diff(unknowns[j]);
				hess[i][j] = df;
			}
		}

		
		System.out.println("Hessian Matrix = ");
		System.out.println(hess);
		System.out.println("Grident = ");
		System.out.println(grad);
		
		//Convert symbolic staff to Bytecode staff to speedup evaluation
		NumMatrix NH = new NumMatrix(hess, unknowns);
		NumVector NG = new NumVector(grad, unknowns);
		
		System.out.println("Iterativly sovle ... ");
		for(int i=0; i<maxIter; i++) {
			//Use JAMA to solve the system
			Matrix A = new Matrix(NH.eval(init));
			Matrix b = new Matrix(NG.eval(init), NG.dim());
			Matrix x = A.solve(b); //Lease Square solution
			for(int j=0; j<init.length; j++) {
				System.out.print(String.format("%s=%.5f",unknowns[j], init[j])+" ");
			}
			System.out.println();
			if(x.norm2() < eps) 
				break;
			//Update initial guess
			for(int j=0; j<init.length; j++) {
				init[j] = init[j] - x.get(j, 0);
			}
		}
	}
}
