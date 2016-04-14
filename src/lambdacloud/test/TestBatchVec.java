package lambdacloud.test;

import static lambdacloud.test.TestUtils.assertEqual;
import static symjava.symbolic.Symbol.x;
import static symjava.symbolic.Symbol.y;
import static symjava.symbolic.Symbol.z;
import lambdacloud.core.lang.LCReturn;
import symjava.bytecode.BytecodeBatchVecFunc;
import symjava.bytecode.BytecodeSelect;
import symjava.bytecode.BytecodeVecFunc;
import symjava.symbolic.Expr;
import symjava.symbolic.Vector;
import symjava.symbolic.utils.JIT;

public class TestBatchVec {

	public static void main(String[] args) {
		testVector();
		testBatchVecFunc();
		testSimpleCartesian();
		testCartesian();
	}
	public static void testVector() {
		Vector x = new Vector("x",3);
		BytecodeVecFunc fun =  CompileUtils.compileVecFunc(new LCReturn(x));
		double[] data_x = new double[] {1,2,3};
		double[] outAry = new double[3];
		fun.apply(outAry, 0, data_x);
		assertEqual(new double[]{1,2,3}, outAry);
	}
	
	public static void testBatchVecFunc() {
		BytecodeVecFunc func = JIT.compileVecFunc(new Expr[]{x,y}, x+y);
		//BytecodeVecFunc func =  CompileUtils.compileVecFunc(x+y, new Expr[]{x,y});
		int dim = 3;
		double[][] args = { {1,2,3}, {4,5,6} };
		double[] outAry = new double[dim];
		func.apply(outAry, 0, args);
		for(double d : outAry) {
			System.out.println(d);
		}
		
		BytecodeBatchVecFunc ff = new BytecodeBatchVecFunc(func, dim, dim);
		double[] outAry2 = new double[dim*3];
		double[][] args2 = { {10,20,30,100,200,300,1000,2000,3000},
							  {4,5,6,4,5,6,4,5,6}
						    };
		ff.apply(outAry2, 0, args2);
		for(double d : outAry2) {
			System.out.println(d);
		}
	}
	
	public static void testSimpleCartesian() {
		BytecodeVecFunc func = JIT.compileVecFunc(new Expr[]{x,y}, x+y);
		int dim = 3;
		double[][] args = { {1,2,3}, {4,5,6} };
		double[] outAry = new double[dim];
		func.apply(outAry, 0, args);
		for(double d : outAry) {
			System.out.println(d);
		}
		
		BytecodeBatchVecFunc ff = new BytecodeBatchVecFunc(func, dim, dim);
		double[][] args2 = BytecodeSelect.cartesion(args);
		double[] outAry2 = new double[args2[0].length];
		ff.apply(outAry2, 0, args2);
		for(double d : outAry2) {
			System.out.println(d);
		}
		assertEqual(new double[]{5,6,7,6,7,8,7,8,9}, outAry2);
	}

	public static void testCartesian() {
		BytecodeVecFunc func = JIT.compileVecFunc(new Expr[]{x,y,z}, x+y+z);
		int dim = 3;
		double[][][] args = { { {1,2,3}, {4,5,6} }, {{0,1,2,3}}};
		
		BytecodeBatchVecFunc ff = new BytecodeBatchVecFunc(func, dim, dim);
		double[][] args2 = BytecodeSelect.cartesion(args);
		double[] outAry2 = new double[args2[0].length];
		ff.apply(outAry2, 0, args2);
		for(double d : outAry2) {
			System.out.println(d);
		}
		assertEqual(new double[]{5,6,7,8,7,8,9,10,9,10,11,12}, outAry2);
	}

}
