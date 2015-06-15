package lambdacloud.test;

import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.lang.LCVar;

public class TestCloudFunc {
	public static void main(String[] args) {
		CloudConfig.setTarget("server1");

		LCVar x = LCVar.getDouble("x");
		LCVar y = LCVar.getDouble("y");

		CloudFunc func = new CloudFunc("func123", new LCVar[]{x,y}, x + y);
		System.out.println(func.isOnCloud());
		
	}
}
