package lambdacloud.core;

import lambdacloud.core.lang.LCDevice;
import symjava.symbolic.Expr;

public class LambdaCloud {
	//Just for test
	public static int maxNumCPU = 3;
	public static int currentCPU = 0;
	public static Expr CPU(Expr expr) {
		expr.runOn(new LCDevice(String.valueOf(currentCPU%maxNumCPU)));
		currentCPU++;//round robin
		return expr;
	}
	
	public static Expr GPU(Expr expr) {
		expr.runOn(new LCDevice("/gpu"));
		return expr;
	}
}
