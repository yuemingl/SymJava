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
		solve(eqs, init, new double[0], maxIter, eps);
	}
	
	public static void solve(Eq[] eqs, double[] init, double[] params, int maxIter, double eps) {
		for(Eq eq : eqs) {
			if(!Symbol.C0.symEquals(eq.rhs())) {
				System.out.println("The right hand side of the equation must be 0.");
				return;
			}
		}
		Expr[] unknowns = eqs[0].getUnknowns();
		Expr[] funParams = eqs[0].getParams();
		if(funParams.length != params.length) {
			throw new RuntimeException("funParams.length != params.length");
		}
		int m = eqs.length;
		int n = unknowns.length;
		
		//Construct Jacobian Matrix
		SymVector lhss = new SymVector(m);
		SymMatrix hess = new SymMatrix(m, n);
		for(int i=0; i<m; i++) {
			for(int j=0; j<n; j++) {
				lhss[i] = eqs[i].lhs();
				Expr df = lhss[i].diff(unknowns[j]);
				hess[i][j] = df;
			}
		}

		System.out.println("Jacobian Matrix = ");
		System.out.println(hess);
		
		//Convert symbolic staff to Bytecode staff to speedup evaluation
		Expr[] args = new Expr[unknowns.length + funParams.length];
		int ii=0;
		for(int j=0; j<unknowns.length; j++)
			args[ii++] = unknowns[j];
		for(int j=0; j<funParams.length; j++)
			args[ii++] = funParams[j];
		NumMatrix NH = new NumMatrix(hess, args);
		NumVector NG = new NumVector(lhss, args);
		
		System.out.println("Iterativly sovle ... ");
		double[] funArgs = new double[args.length];
		for(int j=0; j<init.length;j++)
			funArgs[j] = init[j];
		for(int j=init.length; j<funArgs.length; j++)
			funArgs[j] = params[j-init.length];
		
		double[] outHess = new double[NH.rowDim()*NH.colDim()];
		double[] outRes = new double[NG.dim()];
		for(int i=0; i<maxIter; i++) {
			//Use JAMA to solve the system
			NH.eval(outHess, funArgs);
			Matrix A = new Matrix(NH.copyData());
//			System.out.println("Matrix:");
//			for(int j=0;j<A.getRowDimension();j++) {
//				for(int k=0; k<A.getColumnDimension(); k++) {
//					System.out.println(A.get(j,k)+" ");
//				}
//				System.out.println();
//			}
			Matrix b = new Matrix(NG.eval(outRes, funArgs), NG.dim());
			Matrix x = A.solve(b); //Lease Square solution
			for(int j=0; j<init.length; j++) {
				System.out.print(String.format("%s=%.7f",unknowns[j], funArgs[j])+" ");
			}
			System.out.println();
			if(x.norm2() < eps) 
				break;
			//Update initial guess
			for(int j=0; j<init.length; j++) {
				funArgs[j] = funArgs[j] - x.get(j, 0);
			}
		}
	}
}
