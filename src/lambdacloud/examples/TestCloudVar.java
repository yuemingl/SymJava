package lambdacloud.examples;

import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudVar;

public class TestCloudVar {

	public static void main(String[] args) {
		CloudConfig.setTarget("server");
		
		double[] data = {1,2,3,4,5,6};
		CloudVar var = new CloudVar("var123").init(data);
		var.storeToCloud();
		
		CloudVar var2 = new CloudVar("var123");
		double[] dataVar2 = var2.fetchToLocal();
		for(double d : dataVar2) {
			System.out.println(d);
		}
	}

}
