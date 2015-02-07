package symjava.examples;

import symjava.matrix.SymMatrix;
import symjava.matrix.SymVector;
import symjava.numeric.NumMatrix;
import symjava.numeric.NumVector;
import symjava.relational.Eq;
import symjava.symbolic.Expr;
import symjava.symbolic.Symbol;
import Jama.Matrix;

/**
 * Find root(s) for an equation or a system of equations 
 *
 */
public class Newton {
	public static void solve(Eq[] eqs, double[] init, int maxIter, double eps) {
		for(Eq eq : eqs) {
			if(!Symbol.C0.symEquals(eq.rhs)) {
				System.out.println("The right hand side of the equation must be 0.");
				return;
			}
		}
		Expr[] unknowns = eqs[0].getUnknowns();
		int m = eqs.length;
		int n = unknowns.length;
		
		//Construct Jacobian Matrix
		SymVector lhss = new SymVector(m);
		SymMatrix hess = new SymMatrix(m, n);
		for(int i=0; i<n; i++) {
			for(int j=0; j<n; j++) {
				lhss[i] = eqs[i].lhs;
				Expr df = lhss[i].diff(unknowns[j]);
				hess[i][j] = df;
			}
		}

		System.out.println("Jacobian Matrix = ");
		System.out.println(hess);
		
		//Convert symbolic staff to Bytecode staff to speedup evaluation
		NumMatrix NH = new NumMatrix(hess, unknowns);
		NumVector NG = new NumVector(lhss, unknowns);
		
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
