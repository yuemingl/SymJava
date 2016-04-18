package symjava.bytecode;

public class BytecodeSelect {
	BytecodeVecFunc[] funcs;
	
	public BytecodeSelect(BytecodeVecFunc[] funcs) {
		this.funcs = funcs;
	}

	
//	void apply(double[] outAry, int outPos, double[][] ...args) {
//		int nCols = 0 ;
//		for(int i=0; i<args.length; i++)
//			nCols += args[i].length;
//		int nRows = 1;
//		for(int i=0; i<args.length; i++) {
//			nRows *= args[i][0].length;
//		}
//		
//		double[][] newArgs = new double[nCols][nRows];
//		_apply(args, args.length, newArgs);
//	}
	
	public void cartesian(double[][][] args, int level, int col, double[] buf, double[][] newArgs) {
		if(level == args.length - 1) {
			for(int i=0; i<args[level][0].length; i++) {
				for(int k=0; k<args[level].length; k++)
					buf[col+k] = args[level][k][i];
				for(int j=0; j<buf.length; j++) {
					newArgs[j][row] = buf[j];
				}
				row++;
			}
			return;
		}
		for(int i=0; i<args[level][0].length; i++) {
			for(int k=0; k<args[level].length; k++)
				buf[col+k] = args[level][k][i];
			cartesian(args, level+1, col+args[level].length, buf, newArgs);
		}
	}
	
	public static double[][] cartesian(double[]... args) {
		int colMax = args.length;
		double[] buf = new double[colMax];
		int rowMax = 1;
		for(int i=0; i<args.length; i++) {
			rowMax *= args[i].length;
		}
		double[][] newArgs = new double[colMax][rowMax];
		BytecodeSelect sel = new BytecodeSelect(null);
		sel.simpleCartesian(args, 0, buf, newArgs);
		return newArgs;
	}
	
	public static double[][] cartesian(int vecLen, double[]... args) {
		int colMax = args.length;
		double[][] buf = new double[colMax][vecLen];
		int rowMax = 1;
		for(int i=0; i<args.length; i++) {
			rowMax *= (args[i].length/vecLen);
		}
		rowMax *= vecLen;
		double[][] newArgs = new double[colMax][rowMax];
		BytecodeSelect sel = new BytecodeSelect(null);
		sel.simpleCartesian(vecLen, args, 0, buf, newArgs);
		return newArgs;
	}

	
	public static double[][] cartesian(double[][]... args) {
		int colMax = 0 ;
		for(int i=0; i<args.length; i++)
			colMax += args[i].length;
		double[] buf = new double[colMax];
		int rowMax = 1;
		for(int i=0; i<args.length; i++) {
			rowMax *= args[i][0].length;
		}
		double[][] newArgs = new double[colMax][rowMax];
		BytecodeSelect sel = new BytecodeSelect(null);
		sel.cartesian(args, 0, 0, buf, newArgs);
		return newArgs;
	}
	
	public int row = 0;
	public void simpleCartesian(double[][] args, int col, double[] buf, double[][] newArgs) {
		if(col == args.length - 1) {
			for(int i=0; i<args[col].length; i++) {
				buf[col] = args[col][i];
				for(int j=0; j<buf.length; j++) {
					newArgs[j][row] = buf[j];
				}
				row++;
			}
			return;
		}
		for(int i=0; i<args[col].length; i++) {
			buf[col] = args[col][i];
			simpleCartesian(args, col+1, buf, newArgs);
		}
	}
	
	public void simpleCartesian(int vecLen, double[][] args, int col, double[][] buf, double[][] newArgs) {
		if(col == args.length - 1) {
			for(int i=0; i<args[col].length; i+=vecLen) {
				for(int k=0; k<vecLen; k++)
					buf[col][k] = args[col][i+k];
				for(int j=0; j<buf.length; j++) {
					for(int k=0; k<vecLen; k++)
						newArgs[j][row+k] = buf[j][k];
				}
				row+=vecLen;
			}
			return;
		}
		for(int i=0; i<args[col].length; i+=vecLen) {
			for(int k=0; k<vecLen; k++)
				buf[col][k] = args[col][i+k];
			simpleCartesian(vecLen, args, col+1, buf, newArgs);
		}
	}

	public void testSimpleCartesian() {
		double[][] args = {{1,2},{3,4,5,6},{7,8}};
		int colMax = args.length;
		double[] buf = new double[colMax];
		int rowMax = 1;
		for(int i=0; i<args.length; i++) {
			rowMax *= args[i].length;
		}
		double[][] newArgs = new double[colMax][rowMax];
		simpleCartesian(args, 0, buf, newArgs);
		for(int j=0; j<newArgs[0].length; j++) {
			for(int i=0; i<newArgs.length; i++) {
				System.out.print(newArgs[i][j]+" ");
			}
			System.out.println();
		}
	}

	public void testSimpleCartesian2() {
		double[][] args = {{1,2,3,5,7,9},{4,5,6,7,8,9}};
		double[][] newArgs = cartesian(3, args);
		
		for(int j=0; j<newArgs[0].length; j++) {
			for(int i=0; i<newArgs.length; i++) {
				System.out.print(newArgs[i][j]+" ");
			}
			System.out.println();
		}
	}
	
	public void testCartesian() {
		//double[][][] args = { {{1,2},{3,4}}, {{7,8,9},{5,6,7}} };
		double[][][] args = { {{1,2},{3,4}}, {{7,8,9}}, {{5,6,7}} };
		int colMax = 0 ;
		for(int i=0; i<args.length; i++)
			colMax += args[i].length;
		double[] buf = new double[colMax];
		int rowMax = 1;
		for(int i=0; i<args.length; i++) {
			rowMax *= args[i][0].length;
		}
		double[][] newArgs = new double[colMax][rowMax];
		cartesian(args, 0, 0, buf, newArgs);
		for(int j=0; j<newArgs[0].length; j++) {
			for(int i=0; i<newArgs.length; i++) {
				System.out.print(newArgs[i][j]+" ");
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {
//		//row = 0;
//		BytecodeSelect bs = new BytecodeSelect(null);
//		bs.testSimpleCartesian();
		
//		//row = 0;
//		BytecodeSelect bs2 = new BytecodeSelect(null);
//		bs2.testCartesian();
		
		//row = 0;
		BytecodeSelect bs = new BytecodeSelect(null);
		bs.testSimpleCartesian2();
	}
}
