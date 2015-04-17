package lambdacloud.examples;

import symjava.symbolic.Expr;
import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudFunc;
import static symjava.symbolic.Symbol.*;

public class TestCloudFunc {
	public static void main(String[] args) {
		CloudConfig.setTarget("server1");
		
		CloudFunc func = new CloudFunc("func123", new Expr[]{x,y}, x + y);
		System.out.println(func.isOnCloud());
		
	}
}
