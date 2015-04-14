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
		CloudConfig config = new CloudConfig("local");
		
		Expr expr = x + y;
		CloudFunc f = new CloudFunc(config);
		f.compile(new Expr[]{x, y}, expr);
		
		CloudVar input = new CloudVar(config,"input").init(new double[]{1, 2});
		CloudVar output = new CloudVar(config,"output").resize(1);
		
		long begin = System.currentTimeMillis();
		f.apply(output, input);
		long end = System.currentTimeMillis();
		System.out.println("Time: "+((end-begin)/1000.0));
		for(double d : output.fetchToLocal()) {
			System.out.println(d);
		}
	}
}
