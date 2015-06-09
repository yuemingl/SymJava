package lambdacloud.examples;

import static symjava.symbolic.Symbol.*;

import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.CSD;
import symjava.symbolic.Expr;

public class Test {
	public static void main(String[] args) {
		test();
	}
	
	public static void test() {
		CloudConfig.setTarget("local");
		
		Expr expr = x + y;
		CloudFunc f = new CloudFunc(new Expr[]{x, y}, expr);
		
		CSD input = new CSD("input").init(new double[]{1, 2});
		CSD output = new CSD("output").resize(1);
		
		long begin = System.currentTimeMillis();
		f.apply(output, input);
		long end = System.currentTimeMillis();
		System.out.println("Time: "+((end-begin)/1000.0));
		if(output.fetchToLocal()) {
			for(double d : output.getData()) {
				System.out.println(d);
			}
		}
	}
}
