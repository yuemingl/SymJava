package io.lambdacloud.symjava.examples;

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.sparse.CG;
import no.uib.cipr.matrix.sparse.IterativeSolverNotConvergedException;

public class Solver {
	public static double[] solveCG(double[][] A, double[] b, double[] x) {
		DenseMatrix DA = new DenseMatrix(A);
		DenseVector Db = new DenseVector(b);
		DenseVector Dx = new DenseVector(x);
		CG sol = new CG(Db);
		try {
			long begin = System.currentTimeMillis();
			sol.solve(DA, Db, Dx);
			long end = System.currentTimeMillis();
			System.out.println(String.format("Iter=%03d Time=%dms", sol
					.getIterationMonitor().iterations(), (end - begin)));
		} catch (IterativeSolverNotConvergedException e) {
			e.printStackTrace();
		}
		return Dx.getData();
	}
	public static double[] solveCG2(double[][] _A, double[] _b, double[] _x) {
		//System.out.println("b=");
		//for(int i=0; i<_b.length; i++)
		//	System.out.println(_b[i]+" ");
		//System.out.println();
		
		Matrix A = new DenseMatrix(_A);
		Vector b = new DenseVector(_b);
		DenseVector x = new DenseVector(_x);
		Matrix AT = A.copy().transpose();
		Matrix ATA = new DenseMatrix(AT.numRows(), A.numColumns());
		AT.mult(A, ATA);
		DenseMatrix I = new DenseMatrix(ATA.numRows(), ATA.numColumns());
		for(int i=0;i<ATA.numRows();i++) {
			I.set(i, i, 0.000001*1);
		}
		ATA.add(I);
		Vector ATb = new DenseVector(AT.numRows());
		ATb = AT.mult(b, ATb);
		
		CG sol = new CG(ATb);
		try {
			long begin = System.currentTimeMillis();
			sol.solve(ATA, ATb, x);
			long end = System.currentTimeMillis();
			//System.out.println(String.format("CG Iter=%03d Time=%dms", sol
			//		.getIterationMonitor().iterations(), (end - begin)));
		} catch (IterativeSolverNotConvergedException e) {
			e.printStackTrace();
		}
		System.arraycopy(x.getData(), 0, _x, 0, _x.length);
		return _x;
	}
	
	public static void main(String[] args) {
		double[][] A = {
				{ 2, 1,  3},
				{ 2, 6,  8},
				{ 6, 8, 18}
		};
		double[] b = {1, 3, 5};
		double[] x = {0, 0, 0};
		solveCG2(A, b, x);
		for(double i : x)
			System.out.println(i);
		//x=(0.3,0.4,0)
		
		int N = Integer.valueOf(args[0]);
		long begin = System.currentTimeMillis();
		double[][] AA = new double[N][N];
		double[] xx = new double[N];
		double[] bb = new double[N];
		for(int i=0;i<N;i++)
			for(int j=0;j<N;j++)
				AA[i][j] = Math.random();
		for(int i=0;i<N;i++)
			bb[i] = Math.random();
		solveCG2(AA, bb, xx);
		long end = System.currentTimeMillis();
		System.out.println("Time for solving "+N+"*"+N+" system:" + (end-begin) + "ms");
	}
}
