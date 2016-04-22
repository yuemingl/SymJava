package symjava.symbolic.utils;

import Jama.Matrix;

public class BytecodeOpSupport {
	public static Jama.Matrix concat(Jama.Matrix[] args) {
		int len = 0;
		for(int i=0; i<args.length; i++) {
			len += args[i].getRowDimension();
		}
		double[] data = new double[len];
		int destPos = 0;
		for(int i=0; i<args.length; i++) {
			System.arraycopy(args[i].getColumnPackedCopy(), 0, data, destPos, args[i].getRowDimension());
			destPos += args[i].getRowDimension();
		}
		return new Jama.Matrix(data, len);
	}
	
	//-------------Math functions for Jama.Matrix----------------
	
	public static Jama.Matrix sin(Jama.Matrix arg) {
		double[][] data = arg.getArray();
		for(int i=0; i<data.length; i++) {
			for(int j=0; j<data[0].length; j++) {
				data[i][j] = Math.sin(data[i][j]);
			}
		}
		return arg;
	}

	public static Jama.Matrix abs(Jama.Matrix arg) {
		double[][] data = arg.getArray();
		for(int i=0; i<data.length; i++) {
			for(int j=0; j<data[0].length; j++) {
				data[i][j] = Math.abs(data[i][j]);
			}
		}
		return arg;
	}
	
	public static Jama.Matrix cos(Jama.Matrix arg) {
		double[][] data = arg.getArray();
		for(int i=0; i<data.length; i++) {
			for(int j=0; j<data[0].length; j++) {
				data[i][j] = Math.cos(data[i][j]);
			}
		}
		return arg;
	}
	
	public static Jama.Matrix negate(Jama.Matrix arg) {
		double[][] data = arg.getArray();
		for(int i=0; i<data.length; i++) {
			for(int j=0; j<data[0].length; j++) {
				data[i][j] = -data[i][j];
			}
		}
		return arg;
	}
	
	public static Jama.Matrix log(double base, Jama.Matrix arg) {
		double[][] data = arg.getArray();
		for(int i=0; i<data.length; i++) {
			for(int j=0; j<data[0].length; j++) {
				data[i][j] = Math.log(data[i][j])/Math.log(base);
			}
		}
		return arg;
	}

	public static Matrix makeColumnVector(Matrix m) {	
		if (m.getColumnDimension()==1)
			return m;
		return m.transpose();
	}
	
	public static Matrix dot(Matrix vec1, Matrix vec2) {
		Matrix colVec1 = makeColumnVector(vec1);
		Matrix colVec2 = makeColumnVector(vec2);
		
		int len = colVec1.getRowDimension();
		if (len != colVec2.getRowDimension()) {
			throw new IllegalArgumentException(
					"The two vectors in dot product must have the same number of elements.");
		}
		
		double rlt = 0;
		for (int i=0; i<len; i++) {
			rlt += colVec1.get(i,0) * colVec2.get(i,0);
		}
		Matrix m = new Matrix(new double[]{rlt},1);
		return m;
	}
}
