package lambdacloud.examples;

import static symjava.symbolic.Symbol.*;

import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudVar;
import symjava.symbolic.Expr;

public class Test {
	public static void main(String[] args) {
		test();
	}
	
	public static void test() {
		CloudConfig.setTarget("local");
		
		Expr expr = x + y;
		CloudFunc f = new CloudFunc(new Expr[]{x, y}, expr);
		
		CloudVar input = new CloudVar("input").init(new double[]{1, 2});
		CloudVar output = new CloudVar("output").resize(1);
		
		long begin = System.currentTimeMillis();
		f.apply(output, input);
		long end = System.currentTimeMillis();
		System.out.println("Time: "+((end-begin)/1000.0));
		for(double d : output.fetchToLocal()) {
			System.out.println(d);
		}
	}
}
