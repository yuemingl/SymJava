package symjava.examples;

import symjava.matrix.SymMatrix;
import symjava.matrix.SymVector;
import symjava.numeric.NumMatrix;
import symjava.numeric.NumVector;
import symjava.relational.Eq;
import symjava.symbolic.Expr;
import symjava.symbolic.Symbol;
import symjava.symbolic.utils.JIT;
import Jama.Matrix;

/**
 * Find maximum or minimum point of eq.lhs() by giving a initial guess
 *
 */
public class NewtonOptimization {
	public static double[] solve(Eq eq, double[] initAndOut, int maxIter, double eps, boolean dislpayOnly) {
//		if(!Symbol.C0.symEquals(eq.rhs())) {
//			System.out.println("The right hand side of the equation must be 0.");
//			return null;
//		}
		Expr[] unknowns = eq.getUnknowns();
		int n = unknowns.length;
		
		//Construct Hessian Matrix
		SymVector grad = new SymVector(n);
		SymMatrix hess = new SymMatrix(n, n);
		Expr L = eq.lhs();
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
		
		if(dislpayOnly) return null;
		
		//Convert symbolic staff to Bytecode staff to speedup evaluation
		NumMatrix NH = new NumMatrix(hess, unknowns);
		NumVector NG = new NumVector(grad, unknowns);
		
		System.out.println("Iterativly sovle ... ");
		double[] x = initAndOut;
		double[] dx = new double[initAndOut.length];
		System.arraycopy(initAndOut, 0, dx, 0, initAndOut.length);
		
		double[] outHess = new double[NH.rowDim()*NH.colDim()];
		double[] outRes = new double[NG.dim()];
		for(int i=0; i<maxIter; i++) {
			NH.eval(outHess, x);
			Solver.solveCG2(NH.copyData(), NG.eval(outRes, x), dx);
			Matrix mdx = new Matrix(dx, NG.dim());
			
			//Use JAMA to solve the system
			//Matrix A = new Matrix(NH.eval(x));
			//Matrix b = new Matrix(NG.eval(x), NG.dim());
			//Matrix mdx = A.solve(b); //Lease Square solution
			
			//Update initial guess
			for(int j=0; j<dx.length; j++) {
				x[j] = x[j] - mdx.get(j, 0)*0.5;
			}
			System.out.print("Iter="+i+" ");
			for(int j=0; j<initAndOut.length; j++) {
				System.out.print(String.format("%s=%.5f",unknowns[j], x[j])+" ");
			}
			System.out.println();

			if(mdx.norm2() < eps) 
				break;
		}
		return initAndOut;
	}
}
