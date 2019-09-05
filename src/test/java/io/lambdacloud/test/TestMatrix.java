package io.lambdacloud.test;

import static io.lambdacloud.core.LambdaCloud.CPU;
import static io.lambdacloud.test.TestUtils.*;

import java.util.HashMap;
import java.util.Map;

import io.lambdacloud.core.CloudConfig;
import io.lambdacloud.core.CloudSD;
import io.lambdacloud.core.Session;
import io.lambdacloud.core.lang.LCDevice;
import io.lambdacloud.core.lang.LCReturn;
import io.lambdacloud.symjava.bytecode.BytecodeVecFunc;
import io.lambdacloud.symjava.matrix.ExprMatrix;
import io.lambdacloud.symjava.matrix.ExprVector;
import io.lambdacloud.symjava.symbolic.Concat;
import io.lambdacloud.symjava.symbolic.Expr;
import io.lambdacloud.symjava.symbolic.Matrix;
import io.lambdacloud.symjava.symbolic.Vector;

public class TestMatrix {
	public static void main(String[] args) {
		testBasic1();
		testBasic2();
		testBasic3();
		testBasic4();
		testConcat1();
		testConcat2();
		testMatrixSplit1();
		testMatrixSplit2();
		testMatrixSplit3(new CloudConfig("job_local.conf"));
	}
	
	public static void testBasic1() {
		Matrix A = new Matrix("A",3,3);
		BytecodeVecFunc fun =  CompileUtils.compileVecFunc(new LCReturn(A));
		/**
		 * 1 2 3
		 * 4 5 6
		 * 7 8 9
		 */
		double[] data_A = new double[] {1,4,7,2,5,8,3,6,9}; // Column-wise
		double[] outAry = new double[9];
		fun.apply(outAry, 0, data_A);
		assertEqual(new double[]{1,4,7,2,5,8,3,6,9}, outAry);
	}
	
	public static void testBasic2() {
		Vector x = new Vector("x",3);
		BytecodeVecFunc fun =  CompileUtils.compileVecFunc(new LCReturn(x));
		double[] data_x = new double[] {1,2,3};
		double[] outAry = new double[3];
		fun.apply(outAry, 0, data_x);
		assertEqual(new double[]{1,2,3}, outAry);
	}
	
	public static void testBasic3() {
		Matrix A = new Matrix("A",3,3);
		Vector x = new Vector("x",3);
		BytecodeVecFunc fun =  CompileUtils.compileVecFunc(new LCReturn(A*x));
		/**
		 * 1 2 3
		 * 4 5 6
		 * 7 8 9
		 */
		double[] data_A = new double[] {1,4,7,2,5,8,3,6,9}; // Column-wise
		double[] data_x = new double[] {1,2,3};
		double[] outAry = new double[3];
		fun.apply(outAry, 0, data_A, data_x);
		assertEqual(new double[]{14,32,50}, outAry);
	}
	
	public static void testBasic4() {
		Vector x = new Vector("x",3);
		Vector y = new Vector("y",3);
		
		BytecodeVecFunc fun =  CompileUtils.compileVecFunc(new LCReturn(x+y), x, y);
		double[] outAry = new double[4];
		double[] data_x = new double[] {1,2,3};
		double[] data_y = new double[] {1,2,3};
		fun.apply(outAry, 1, data_x, data_y); //output at position 1
		assertEqual(new double[]{0,2,4,6}, outAry);
	}
	
	public static void testConcat1() {
		Vector x = new Vector("x",3);
		Vector y = new Vector("y",2);
		Vector z = new Vector("z",4);
		
		BytecodeVecFunc fun =  CompileUtils.compileVecFunc(new LCReturn(new Concat(x,y,z)), x, y, z);
		double[] outAry = new double[9];
		double[] data_x = new double[] {1,2,3};
		double[] data_y = new double[] {4,5};
		double[] data_z = new double[] {6,7,8,9};
		fun.apply(outAry, 0, data_x, data_y, data_z);
		assertEqual(new double[]{1,2,3,4,5,6,7,8,9}, outAry);
	}
	
	public static void testConcat2() {
		Vector x = new Vector("x",3);
		Vector y = new Vector("y",2);
		Vector z = new Vector("z",5);
		
		BytecodeVecFunc fun =  CompileUtils.compileVecFunc(new LCReturn(new Concat(x,y)+z), x, y, z);
		double[] outAry = new double[5];
		double[] data_x = new double[] {1,2,3};
		double[] data_y = new double[] {4,5};
		double[] data_z = new double[] {1,2,3,4,5};
		fun.apply(outAry, 0, data_x, data_y, data_z);
		assertEqual(new double[]{2,4,6,8,10}, outAry);
	}
	
	public static void testMatrixSplit1() {
		int dim = 4;
		Matrix A = new Matrix("A", dim, dim);
		Vector x = new Vector("x", dim);
		Vector y0 = new Vector("y0", dim);

		ExprMatrix AA = A.split(2, 2);
		ExprVector xx = x.split(2);
		ExprVector yy = (ExprVector)(AA*xx);
		System.out.println(yy);
		yy[0].runOn(new LCDevice(0));
		yy[1].runOn(new LCDevice(1));
		
		Expr res = new Concat(yy[0],yy[1])+y0;
		
		System.out.println(res);
		//Doesn't work because the symbol AA[0][0] has no name?
		//BytecodeVecFunc fun = CompileUtils.compileVec(new LCReturn(res), A,x,y0,AA[0][0],AA[1][0],xx[0],xx[1]);
		BytecodeVecFunc fun = CompileUtils.compileVecFunc(new LCReturn(res));
/*
1 2 3 4   0   1   13
1 2 1 3 * 1 + 2 = 9
1 2 2 1   2   3   10
2 3 1 4   1   4   13
*/
		double[] outAry = new double[4];
		double[] data_A_11 = new double[] {2,1,1,4};
		double[] data_A_10 = new double[] {1,2,2,3};
		double[] data_A_01 = new double[] {3,1,4,3};
		double[] data_A_00 = new double[] {1,1,2,2};
		double[] data_x_0 = new double[] {0,1};
		double[] data_x_1 = new double[] {2,1};
		double[] data_y0 = new double[] {1,2,3,4};
		//void apply(double[] output, int outPos, double[] A_1_1, double[] A_1_0, double[] A_0_1, double[] A_0_0, double[] x_0, double[] x_1, double[] y0);
		fun.apply(outAry, 0, data_A_11, data_A_10, data_A_01, data_A_00, data_x_0, data_x_1, data_y0);
		assertEqual(new double[]{13,9,10,13}, outAry);
	}
	
	public static void testMatrixSplit2() {
		int dim = 4;
		Matrix A = new Matrix("A", dim, dim);
		Vector x = new Vector("x", dim);
		Vector y0 = new Vector("y0", dim);

		ExprMatrix AA = A.split(2, 2);
		ExprVector xx = x.split(2);
		ExprVector yy = (ExprVector)(AA*xx);
		System.out.println(yy);
		yy[0].runOn(new LCDevice(0));
		yy[1].runOn(new LCDevice(1));
		
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
		
		//those parameters should be able automatically generated according to the definition of AA and xx
		//see testMatrixSplit3()
		double[] data_A_11 = new double[] {2,1,1,4};
		double[] data_A_10 = new double[] {1,2,2,3};
		double[] data_A_01 = new double[] {3,1,4,3};
		double[] data_A_00 = new double[] {1,1,2,2};
		double[] data_x_0 = new double[] {0,1};
		double[] data_x_1 = new double[] {2,1};
		dict.put(AA[0][0].toString(), data_A_00);
		dict.put(AA[0][1].toString(), data_A_01);
		dict.put(AA[1][0].toString(), data_A_10);
		dict.put(AA[1][1].toString(), data_A_11);
		dict.put(xx[0].toString(), data_x_0);
		dict.put(xx[1].toString(), data_x_1);
		
		Session sess1 = new Session();
		CloudSD rlt = sess1.runVec(res, dict);
		rlt.fetch();
		assertEqual(new double[]{13,9,10,13}, rlt.getData());
	}
	
	/**
	 * Automatic data dict split for matrices and vectors
	 */
	public static void testMatrixSplit3(CloudConfig config) {
		int dim = 4;
		Matrix A = new Matrix("A", dim, dim);
		Vector x = new Vector("x", dim);
		Vector y0 = new Vector("y0", dim);

		ExprMatrix AA = A.split(2, 2);
		ExprVector xx = x.split(2);
		// yy = AA * xx
		ExprVector yy = (ExprVector)(AA*xx);
		// res = yy + y0
		Expr res = CPU( new Concat( CPU(yy[0]), CPU(yy[1]) )+y0 );
		System.out.println("Test: res="+res);
		
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
		
		Session sess = new Session(config);
		CloudSD rlt = sess.runVec(res, dict);
		rlt.fetch();
		assertEqual(new double[]{13,9,10,13}, rlt.getData());
		/**
Using config file: /Users/yueming.liu/workspace/eclipse_kepler/SymJava/conf/job_local.conf
127.0.0.1:8322
127.0.0.1:8323
127.0.0.1:8324
Test: res=[A_0_0*x_0 + A_0_1*x_1, A_1_0*x_0 + A_1_1*x_1] + y0
Generating bytecode for: symjava.bytecode.cfunc4
void apply(double[] output, int outPos, double[] A_1_1, double[] A_1_0, double[] x_0, double[] x_1) = return A_1_0*x_0 + A_1_1*x_1
getIR(): create a new instance to test for: return A_1_0*x_0 + A_1_1*x_1
Received CloudFuncResp: symjava.bytecode.cfunc4
Generating bytecode for: symjava.bytecode.cfunc5
void apply(double[] output, int outPos, double[] A_0_1, double[] A_0_0, double[] x_0, double[] x_1) = return A_0_0*x_0 + A_0_1*x_1
getIR(): create a new instance to test for: return A_0_0*x_0 + A_0_1*x_1
Received CloudFuncResp: symjava.bytecode.cfunc5
Generating bytecode for: symjava.bytecode.cfunc6
void apply(double[] output, int outPos, double[] __vec_3, double[] __vec_2, double[] y0) = return [__vec_2, __vec_3] + y0
getIR(): create a new instance to test for: return [__vec_2, __vec_3] + y0
Received CloudFuncResp: symjava.bytecode.cfunc6
>>Session eval: cfunc4=A_1_0*x_0 + A_1_1*x_1; args:
[	A_1_1 = [2.0, 1.0, 1.0, 4.0] (Local)
	A_1_0 = [1.0, 2.0, 2.0, 3.0] (Local)
	x_0 = [0.0, 1.0] (Local)
	x_1 = [2.0, 1.0] (Local)
]Received CloudSDResp: A_1_1
Received CloudSDResp: A_1_0
Received CloudSDResp: x_0
Received CloudSDResp: x_1
Received CloudSD: csd://127.0.0.1:8323/csd16 = [] (on Cloud)
Fetch data ( Connected to 127.0.0.1:8323 )
Received CloudSD: csd://127.0.0.1:8323/csd16 = [7.0, 9.0] (on Cloud & Local)
Return: [7.0 9.0 ]
>>Session eval: cfunc5=A_0_0*x_0 + A_0_1*x_1; args:
[	A_0_1 = [3.0, 1.0, 4.0, 3.0] (Local)
	A_0_0 = [1.0, 1.0, 2.0, 2.0] (Local)
	x_0 = [0.0, 1.0] (Local)
	x_1 = [2.0, 1.0] (Local)
]Received CloudSDResp: A_0_1
Received CloudSDResp: A_0_0
Received CloudSDResp: x_0
Received CloudSDResp: x_1
Received CloudSD: csd://127.0.0.1:8322/csd22 = [] (on Cloud)
Fetch data ( Connected to 127.0.0.1:8322 )
Received CloudSD: csd://127.0.0.1:8322/csd22 = [12.0, 7.0] (on Cloud & Local)
Return: [12.0 7.0 ]
>>Session eval: cfunc6=[__vec_2, __vec_3] + y0; args:
[	csd://127.0.0.1:8323/csd16 = [7.0, 9.0] (on Cloud & Local)
	csd://127.0.0.1:8322/csd22 = [12.0, 7.0] (on Cloud & Local)
	y0 = [1.0, 2.0, 3.0, 4.0] (Local)
]Received CloudSDResp: y0
Received CloudSD: csd://127.0.0.1:8324/csd7 = [] (on Cloud)
Fetch data ( Connected to 127.0.0.1:8324 )
Received CloudSD: csd://127.0.0.1:8324/csd7 = [13.0, 9.0, 10.0, 13.0] (on Cloud & Local)
Return: [13.0 9.0 10.0 13.0 ]
Fetch data ( Connected to 127.0.0.1:8324 )
Received CloudSD: csd://127.0.0.1:8324/csd7 = [13.0, 9.0, 10.0, 13.0] (on Cloud & Local)
Passed!

		 */
	}

}
