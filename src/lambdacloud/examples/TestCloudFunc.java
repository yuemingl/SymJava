package lambdacloud.examples;

import symjava.symbolic.Expr;
import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudVar;
import static symjava.math.SymMath.*;
import static symjava.symbolic.Symbol.*;

public class TestCloudFunc {
	public static void main(String[] args) {
		CloudConfig.setTarget("server1");
		
		double[] data = {1,2,3};
		CloudVar var = new CloudVar("var123").init(data);
		var.storeToCloud();
		
		CloudFunc func = new CloudFunc("func123", new Expr[]{x,y}, sqrt(x*x+y*y)-100);
		System.out.println(Thread.currentThread().getName());
		
		CloudFunc func2 = new CloudFunc("func456", new Expr[]{x,y}, sqrt(x*x+y*y)-100);
		System.out.println(Thread.currentThread().getName());
		

		//CloudConfig.shutDown();
	}
}
