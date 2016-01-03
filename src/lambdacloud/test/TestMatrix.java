package lambdacloud.test;

import static symjava.symbolic.Symbol.x;
import static symjava.symbolic.Symbol.y;

import java.util.HashMap;
import java.util.Map;

import lambdacloud.core.CloudConfig;
import lambdacloud.core.Session;
import lambdacloud.core.graph.GraphBuilder;
import lambdacloud.core.graph.Node;
import lambdacloud.core.lang.LCDevice;
import symjava.bytecode.BytecodeBatchFunc;
import symjava.bytecode.BytecodeFunc;
import symjava.matrix.SymMatrix;
import symjava.matrix.SymVector;
import symjava.symbolic.Concat;
import symjava.symbolic.Expr;
import symjava.symbolic.Matrix;
import symjava.symbolic.Vector;

public class TestMatrix {
	public static void main(String[] args) {
//		test1();
//		test2();
//		test3();
//		test4();
		test5();
		test6();
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
	
	public static void test3() {
		Vector x = new Vector("x",3);
		Vector y = new Vector("y",2);
		Vector z = new Vector("z",3);
		
		BytecodeBatchFunc fun =  CompileUtils.compileVec(new Concat(x,y,z), x, y, z);
		double[] outAry = new double[9];
		double[] data_x = new double[] {1,2,3}; //columewise
		double[] data_y = new double[] {4,5};
		double[] data_z = new double[] {6,7,8};
		fun.apply(outAry, 0, data_x, data_y, data_z);
		for(double i : outAry)
			System.out.println(i);
	}
	
	public static void test4() {
		Vector x = new Vector("x",3);
		Vector y = new Vector("y",2);
		Vector z = new Vector("z",5);
		
		BytecodeBatchFunc fun =  CompileUtils.compileVec(new Concat(x,y)+z, x, y, z);
		double[] outAry = new double[9];
		double[] data_x = new double[] {1,2,3}; //columewise
		double[] data_y = new double[] {4,5};
		double[] data_z = new double[] {1,2,3,4,5};
		fun.apply(outAry, 0, data_x, data_y, data_z);
		for(double i : outAry)
			System.out.println(i);
	}
	public static void test5() {
		int dim = 4;
		Matrix A = new Matrix("A", dim, dim);
		Vector x = new Vector("x", dim);
		Vector y0 = new Vector("y0", dim);

		SymMatrix AA = A.split(2, 2);
		SymVector xx = x.split(2);
		SymVector yy = (SymVector)(AA*xx);
		System.out.println(yy);
		yy[0].runOn(new LCDevice("/cpu:0"));
		yy[1].runOn(new LCDevice("/cpu:0"));
		
		Expr res = new Concat(yy[0],yy[1])+y0;
		
		System.out.println(res);
		//BytecodeBatchFunc fun = CompileUtils.compileVec(res, A,x,y0,AA[0][0],AA[1][0],xx[0],xx[1]);
		BytecodeBatchFunc fun = CompileUtils.compileVec(res);
		//void apply(double[] output, int outPos, double[] A_1_1, double[] A_1_0, double[] A_0_1, double[] A_0_0, double[] x_0, double[] x_1, double[] y0);
/*
1 2 3 4   0   1   13
1 2 1 3 * 1 + 2 = 9
1 2 2 1   2   3   10
2 3 1 4   1   4   13
*/
		double[] outAry = new double[4];
		double[] data_A_11 = new double[] {2,1,1,4}; //columewise
		double[] data_A_10 = new double[] {1,2,2,3}; //columewise
		double[] data_A_01 = new double[] {3,1,4,3}; //columewise
		double[] data_A_00 = new double[] {1,1,2,2}; //columewise
		double[] data_x_0 = new double[] {0,1};
		double[] data_x_1 = new double[] {2,1};
		double[] data_y0 = new double[] {1,2,3,4};
		fun.apply(outAry, 0, data_A_11, data_A_10, data_A_01, data_A_00, data_x_0, data_x_1, data_y0);
		for(double i : outAry)
			System.out.println(i); 
		//13.0
		//9.0
		//10.0
		//13.0
	}
	
	public static void test6() {
		int dim = 4;
		Matrix A = new Matrix("A", dim, dim);
		Vector x = new Vector("x", dim);
		Vector y0 = new Vector("y0", dim);

		SymMatrix AA = A.split(2, 2);
		SymVector xx = x.split(2);
		SymVector yy = (SymVector)(AA*xx);
		System.out.println(yy);
		yy[0].runOn(new LCDevice("/cpu:0"));
		yy[1].runOn(new LCDevice("/cpu:0"));
		
		Expr res = new Concat(yy[0],yy[1])+y0;
		
		Map<String, double[]> dict = new HashMap<String, double[]>();
		/*
		1 2 3 4   0   1   13
		1 2 1 3 * 1 + 2 = 9
		1 2 2 1   2   3   10
		2 3 1 4   1   4   13
		*/
		dict.put(A.toString(), new double[]{1,1,1,2,2,2,2,3,3,1,2,1,4,3,1,4});
		dict.put(x.toString(), new double[]{0,1,2,1});
		dict.put(y0.toString(), new double[]{1,2,3,4});
		
		//test
		//those parameters should be able automatically generated accroding to the definition of AA and xx
		double[] data_A_11 = new double[] {2,1,1,4}; //columewise
		double[] data_A_10 = new double[] {1,2,2,3}; //columewise
		double[] data_A_01 = new double[] {3,1,4,3}; //columewise
		double[] data_A_00 = new double[] {1,1,2,2}; //columewise
		double[] data_x_0 = new double[] {0,1};
		double[] data_x_1 = new double[] {2,1};
		dict.put(AA[0][0].toString(), data_A_00);
		dict.put(AA[0][1].toString(), data_A_01);
		dict.put(AA[1][0].toString(), data_A_10);
		dict.put(AA[1][1].toString(), data_A_11);
		dict.put(xx[0].toString(), data_x_0);
		dict.put(xx[1].toString(), data_x_1);
		
		CloudConfig.setGlobalTarget("job_local.conf");
		Node n = GraphBuilder.build(res);
		Session sess1 = new Session();
		double[] rlt = sess1.runVec(n, dict);
		for(double d : rlt)
			System.out.println(d);


	}
}
