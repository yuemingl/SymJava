package io.lambdacloud.test;

import static io.lambdacloud.core.LambdaCloud.CPU;
import static io.lambdacloud.symjava.math.SymMath.sqrt;
import static io.lambdacloud.symjava.symbolic.Symbol.x;
import static io.lambdacloud.symjava.symbolic.Symbol.y;
import static io.lambdacloud.symjava.symbolic.Symbol.z;

import java.util.HashMap;
import java.util.Map;

import io.lambdacloud.core.CloudConfig;
import io.lambdacloud.core.CloudFunc;
import io.lambdacloud.core.CloudSD;
import io.lambdacloud.core.Session;
import io.lambdacloud.symjava.symbolic.Expr;

public class TestThread {

	public static void main(String[] args) {
		test1();
		test2();
		testMatrixSplit3();
	}
	public static void test1() {
		CloudConfig config = new CloudConfig();
		//make sure the expression can be run on different thread
		Expr expr = CPU(x*x);
		System.out.println(expr);
		
		CloudFunc func = new CloudFunc(config, expr);
		CloudSD input = new CloudSD().init(new double[]{3});
		CloudSD output = new CloudSD();
		func.apply(output, input);
		if(output.fetch()) {
			System.out.println(output);
		}
	}
	
	/**
	 * Test multi-thread
	 */
	public static void test2() {
		CloudConfig config = new CloudConfig();
		Expr sum = CPU(x*x) + CPU(y*y) + CPU(z*z) + CPU(x+y+z);
		//Expr sum = CPU(x*x) + CPU(y+z);
		//Expr sum = CPU(x) + CPU(y) + CPU(z) + CPU(x);
		Expr expr = CPU(sqrt(sum));
		System.out.println(expr);
		
		Session sess = new Session(config);
		Map<String, Double> dict = new HashMap<String, Double>();
		dict.put(x.toString(), 2.0);
		dict.put(y.toString(), 3.0);
		dict.put(z.toString(), 4.0);
		
		double d = 0.0;
		d = sess.runSimpleAsync(expr, dict);
		System.out.println(d); //6.164414002968976
		
		d = sess.runSimple(expr, dict);
		System.out.println(d); //6.164414002968976
		
		d = sess.runLocal(expr, dict);
		System.out.println(d); //6.164414002968976
	}
	
	public static void testMatrixSplit3() {
		CloudConfig config = new CloudConfig();
		TestMatrix.testMatrixSplit3(config);
		/**
Using 'local' config.
Test: res=[A_0_0*x_0 + A_0_1*x_1, A_1_0*x_0 + A_1_1*x_1] + y0
Generating bytecode for: symjava.bytecode.cfunc12
void apply(double[] output, int outPos, double[] A_0_1, double[] A_0_0, double[] x_0, double[] x_1) = return A_0_0*x_0 + A_0_1*x_1
Generating bytecode for: symjava.bytecode.cfunc13
void apply(double[] output, int outPos, double[] A_1_1, double[] A_1_0, double[] x_0, double[] x_1) = return A_1_0*x_0 + A_1_1*x_1
Generating bytecode for: symjava.bytecode.cfunc14
void apply(double[] output, int outPos, double[] __vec_12, double[] __vec_13, double[] y0) = return [__vec_12, __vec_13] + y0
>>Session eval: cfunc12=A_0_0*x_0 + A_0_1*x_1; args:
[	A_0_1 = [3.0, 1.0, 4.0, 3.0] (Local)
	A_0_0 = [1.0, 1.0, 2.0, 2.0] (Local)
	x_0 = [0.0, 1.0] (Local)
	x_1 = [2.0, 1.0] (Local)
]Fetching: [csd24 = [] (Local)]
	>>>Thread-11 evaluating cfunc12...; Return: csd24 = [] (Local)
Fetched: [csd24 = [12.0, 7.0] (Local)]
Return: [12.0 7.0 ]
>>Session eval: cfunc13=A_1_0*x_0 + A_1_1*x_1; args:
[	A_1_1 = [2.0, 1.0, 1.0, 4.0] (Local)
	A_1_0 = [1.0, 2.0, 2.0, 3.0] (Local)
	x_0 = [0.0, 1.0] (Local)
	x_1 = [2.0, 1.0] (Local)
]Fetching: [csd25 = [] (Local)]
	>>>Thread-12 evaluating cfunc13...; Return: csd25 = [] (Local)
Fetched: [csd25 = [7.0, 9.0] (Local)]
Return: [7.0 9.0 ]
>>Session eval: cfunc14=[__vec_12, __vec_13] + y0; args:
[	csd24 = [12.0, 7.0] (Local)
	csd25 = [7.0, 9.0] (Local)
	y0 = [1.0, 2.0, 3.0, 4.0] (Local)
]Fetching: [csd26 = [] (Local)]
Fetched without waiting: [csd24 = [12.0, 7.0] (Local)]
Fetched without waiting: [csd25 = [7.0, 9.0] (Local)]
	>>>Thread-13 evaluating cfunc14...; Return: csd26 = [] (Local)
Fetched: [csd26 = [13.0, 9.0, 10.0, 13.0] (Local)]
Return: [13.0 9.0 10.0 13.0 ]
Fetched without waiting: [csd26 = [13.0, 9.0, 10.0, 13.0] (Local)]
Passed!
		 */
	}	
}
