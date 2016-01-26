package symjava.symbolic.utils;

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
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
