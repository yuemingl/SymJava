package lambdacloud.test;

import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudSD;
import lambdacloud.core.lang.LCVar;
import symjava.symbolic.Expr;

public class Test {
	public static void main(String[] args) {
		test();
	}
	
	public static void test() {
		CloudConfig.setTarget("server");
		
		LCVar x = LCVar.getDouble("x");
		LCVar y = LCVar.getDouble("y");
		
		Expr expr = x + y;
		CloudFunc f = new CloudFunc(new LCVar[]{x, y}, expr);
		
		CloudSD input = new CloudSD("input").init(new double[]{1, 2});
		CloudSD output = new CloudSD("output").resize(1);
		
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
