package lambdacloud.examples;

import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudVar;

public class TestCloudVar {

	public static void main(String[] args) {
		CloudConfig.setTarget("local");
		
		double[] data = {1,2,3};
		CloudVar var = new CloudVar("var123").init(data);
		var.storeToCloud();
	}

}
