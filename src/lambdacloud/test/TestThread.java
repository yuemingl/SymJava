package lambdacloud.test;

import static lambdacloud.core.LambdaCloud.CPU;
import static symjava.math.SymMath.sqrt;
import static symjava.symbolic.Symbol.*;

import java.util.HashMap;
import java.util.Map;

import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudSD;
import lambdacloud.core.Session;
import symjava.symbolic.Expr;

public class TestThread {

	public static void main(String[] args) {
		test1();
		test2();
	}
	public static void test1() {
		CloudConfig config = new CloudConfig("local");
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
	
	public static void test2() {
		CloudConfig config = new CloudConfig("local");
		Expr sum = CPU(x*x) + CPU(y*y) + CPU(z*z) + CPU(x+y+z);
		//Expr sum = CPU(x) + CPU(y) + CPU(z) + CPU(x);
		Expr expr = CPU(sqrt(sum));
		System.out.println(expr);
		
		Session sess = new Session(config);
		Map<String, Double> dict = new HashMap<String, Double>();
		dict.put(x.toString(), 2.0);
		dict.put(y.toString(), 3.0);
		dict.put(z.toString(), 4.0);
		
		double d = sess.runSimpleAsync(expr, dict);
		System.out.println(d);
	}	
}
