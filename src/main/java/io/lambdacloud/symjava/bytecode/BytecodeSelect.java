package io.lambdacloud.symjava.bytecode;

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
	
	public void _cartesian(double[][][] args, int level, int col, double[] buf, 
			double[][] newArgs) {
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
			_cartesian(args, level+1, col+args[level].length, buf, newArgs);
		}
	}
	
	public void _cartesian(int vecLen, double[][][] args, int level, int col, double[][] buf, 
			double[][] newArgs) {
		if(level == args.length - 1) {
			for(int i=0; i<args[level][0].length; i+=vecLen) {
				for(int j=0; j<args[level].length; j++) {
					for(int k=0; k<vecLen; k++)
						buf[col+j][k] = args[level][j][i+k];
				}
				for(int j=0; j<buf.length; j++) {
					for(int k=0; k<vecLen; k++)
						newArgs[j][row+k] = buf[j][k];
				}
				row+=vecLen;
			}
			return;
		}
		//row level
		for(int i=0; i<args[level][0].length; i+=vecLen) {
			//column level
			for(int j=0; j<args[level].length; j++) {
				//vector level
				for(int k=0; k<vecLen; k++)
					buf[col+j][k] = args[level][j][i+k];
			}
			_cartesian(vecLen, args, level+1, col+args[level].length, buf, newArgs);
		}
	}
	
	public int row = 0;
	public void _simpleCartesian(double[][] args, int col, double[] buf, 
			double[][] newArgs) {
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
			_simpleCartesian(args, col+1, buf, newArgs);
		}
	}
	
	public void _simpleCartesian(int vecLen, double[][] args, int col, double[][] buf, 
			double[][] newArgs) {
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
			_simpleCartesian(vecLen, args, col+1, buf, newArgs);
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
		sel._simpleCartesian(args, 0, buf, newArgs);
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
		sel._simpleCartesian(vecLen, args, 0, buf, newArgs);
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
		sel._cartesian(args, 0, 0, buf, newArgs);
		return newArgs;
	}
	
	public static double[][] cartesian(int vecLen, double[][]... args) {
		int colMax = 0 ;
		for(int i=0; i<args.length; i++)
			colMax += args[i].length;
		double[][] buf = new double[colMax][vecLen];
		int rowMax = 1;
		for(int i=0; i<args.length; i++) {
			rowMax *= args[i][0].length/vecLen;
		}
		rowMax *= vecLen;
		double[][] newArgs = new double[colMax][rowMax];
		BytecodeSelect sel = new BytecodeSelect(null);
		sel._cartesian(vecLen, args, 0, 0, buf, newArgs);
		return newArgs;
	}
	
	public void testSimpleCartesian() {
		double[][] args = {{1,2},{3,4,5,6},{7,8}};
		double[][] newArgs = cartesian(args);
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
		double[][] newArgs = cartesian(args);
		for(int j=0; j<newArgs[0].length; j++) {
			for(int i=0; i<newArgs.length; i++) {
				System.out.print(newArgs[i][j]+" ");
			}
			System.out.println();
		}
	}
	
	public void testCartesian2() {
		double[][][] args = { {{1,2,3,4,5,6},{7,8,9,10,11,12}}, {{10,20,30,40,50,60}}};
		int vecLen = 3;
		double[][] newArgs = cartesian(vecLen, args);
		for(int j=0; j<newArgs[0].length; j++) {
			for(int i=0; i<newArgs.length; i++) {
				System.out.print(newArgs[i][j]+" ");
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {
		BytecodeSelect bs = new BytecodeSelect(null);
		bs.testSimpleCartesian();
		
		BytecodeSelect bs2 = new BytecodeSelect(null);
		bs2.testCartesian();
		
		BytecodeSelect bs3 = new BytecodeSelect(null);
		bs3.testSimpleCartesian2();
		
		BytecodeSelect bs4 = new BytecodeSelect(null);
		bs4.testCartesian2();
	}
}
