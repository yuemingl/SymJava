package symjava.examples;

import Jama.Matrix;
import symjava.bytecode.BytecodeVecFunc;
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
	
	/**
	 * Compile all the expressions
	 * 
	 * @param eq
	 * @param init
	 * @param data
	 * @param maxIter
	 * @param eps
	 * @return
	 */
	public static double[] solve2(Eq eq, double[] init, double[][] data, int maxIter, double eps) {
		int n = data.length;
		
		//Construct Jacobian Matrix and Residuals
		ExprVector res = new ExprVector(n);
		ExprMatrix J = new ExprMatrix(n, eq.getParams().length);
		
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

	/**
	 * Compile the minimal expressions
	 * 
	 * @param eq
	 * @param init
	 * @param data
	 * @param maxIter
	 * @param eps
	 * @return
	 */
	public static double[] solve(Eq eq, double[] init, double[][] data, int maxIter, double eps) {
		int n = data.length;
		
		//Construct template function for Jacobian Matrix and Residuals
		// res = y - f(x);
		Expr res = eq.lhs() - eq.rhs(); 
		// The jth column of Jacobian matrix = res.diff(params[i])
		Expr[] params = eq.getParams();
		Expr[] colsOfJac = new Expr[params.length];
		for(int j=0; j<params.length; j++) {
			colsOfJac[j]  = res.diff(params[j]);
		}
		
		System.out.println("Arguments: "+Utils.joinLabels(eq.getAllArgs(),", "));
		System.out.println("Jacobian = ");
		System.out.println(Utils.joinLabels(colsOfJac, ","));
		System.out.println("Residuals = ");
		System.out.println(res);
		
//		System.out.println(Utils.joinLabels(eq.getParams(),", "));
//		System.out.println(Utils.joinLabels(eq.getFreeVars(),", "));
//		System.out.println(Utils.joinLabels(eq.getDependentVars(),", "));
//		System.out.println(Utils.joinLabels(eq.getUnknowns(),", "));
		
		BytecodeVecFunc[] fColsOfJac = new BytecodeVecFunc[colsOfJac.length];
		for(int j=0; j<params.length; j++) {
			fColsOfJac[j] = JIT.compileVecFunc(eq.getAllArgs(), colsOfJac[j]);
		}
		BytecodeVecFunc fRes = JIT.compileVecFunc(eq.getAllArgs(), res);
		
		
		double[] outRes = new double[n];
		double[] outJac = new double[init.length * n];
		double[][] fArgs = new double[init.length + data[0].length][n];
		for(int i=0; i<n; i++) {
			for(int j=0; j<init.length; j++)
				fArgs[j][i] = init[j];
			for(int j=init.length; j<init.length + data[0].length; j++)
				fArgs[j][i] = data[i][j-init.length];
		}
		fRes.apply(outRes, 0, fArgs);
		for(int i=0; i<fColsOfJac.length; i++) {
			fColsOfJac[i].apply(outJac,n*i,fArgs);
		}
		
		
		System.out.println("Iterativly sovle ... ");
		for(int k=0; k<maxIter; k++) {
			for(int i=0; i<n; i++) {
				for(int j=0; j<init.length; j++)
					fArgs[j][i] = init[j];
			}
			fRes.apply(outRes, 0, fArgs);
			for(int i=0; i<fColsOfJac.length; i++) {
				fColsOfJac[i].apply(outJac,n*i,fArgs);
			}
			
			//Use JAMA to solve the system
			Matrix A = new Matrix(outJac, n);
			Matrix b = new Matrix(outRes, n);
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
