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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
