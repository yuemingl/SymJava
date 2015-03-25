package symjava.examples;

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
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
}
