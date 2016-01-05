package lambdacloud.core;

import lambdacloud.core.lang.LCDevice;
import symjava.symbolic.Expr;

public class LambdaCloud {
	public static Expr CPU(Expr expr) {
		expr.runOn(new LCDevice("/cpu"));
		return expr;
	}
	public static Expr GPU(Expr expr) {
		expr.runOn(new LCDevice("/gpu"));
		return expr;
	}
}
