package io.lambdacloud.test;

import io.lambdacloud.core.CloudConfig;
import io.lambdacloud.core.CloudFunc;
import io.lambdacloud.core.CloudSD;
import io.lambdacloud.core.lang.LCVar;
import io.lambdacloud.symjava.symbolic.Expr;

public class TestLCVar {
	public static void main(String[] args) {
		test();
	}
	
	public static void test() {
		CloudConfig.setGlobalConfig("job_local.conf");
		
		//declare local variables
		LCVar x = LCVar.getDouble("x");
		LCVar y = LCVar.getDouble("y");
		LCVar z = LCVar.getDouble("z");
		
		Expr expr = x + y + z;
		//x,y are not local now since they are passed as arguments
		CloudFunc f = new CloudFunc(expr, new LCVar[]{x, y});
		//CloudFunc f = new CloudFunc(expr, new LCVar[]{x, y, z});
		
		CloudSD input = new CloudSD("input").init(new double[]{1, 2, 3});
		CloudSD output = new CloudSD();
		
		long begin = System.currentTimeMillis();
		f.apply(output, input);
		long end = System.currentTimeMillis();
		System.out.println("Time: "+((end-begin)/1000.0));
		if(output.fetch()) {
			for(double d : output.getData()) {
				System.out.println(d);
			}
		}
	}
}
