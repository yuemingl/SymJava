package lambdacloud.test;

import symjava.bytecode.BytecodeBatchFunc;
import symjava.bytecode.BytecodeFunc;
import symjava.symbolic.Matrix;
import symjava.symbolic.Vector;

public class TestMatrix {
	public static void main(String[] args) {
		test1();
		test2();
	}
	public static void test1() {
		// TODO Auto-generated method stub
		Matrix A = new Matrix("A",3,3);
		Vector x = new Vector("x",3);
		
		//CompileUtils.compile("test1", A, A);
		//CompileUtils.compile("test2", x, x);
		
		//BytecodeFunc fun = CompileUtils.compile("test2", A*x, A, x);
		//double ret = fun.apply(new double[9]);
		//System.out.println(ret);
		
		BytecodeBatchFunc fun =  CompileUtils.compileVec(A*x, A, x);
		double[] outAry = new double[4];
		double[] data_A = new double[] {1,4,7,2,5,8,3,6,9}; //columewise
		double[] data_x = new double[] {1,2,3};
		fun.apply(outAry, 1, data_A, data_x);
		for(double i : outAry)
			System.out.println(i);
	}
	
	public static void test2() {
		Vector x = new Vector("x",3);
		Vector y = new Vector("y",3);
		
		BytecodeBatchFunc fun =  CompileUtils.compileVec(x+y, x, y);
		double[] outAry = new double[4];
		double[] data_x = new double[] {1,2,3}; //columewise
		double[] data_y = new double[] {1,2,3};
		fun.apply(outAry, 0, data_x, data_y);
		for(double i : outAry)
			System.out.println(i);
	}
}
