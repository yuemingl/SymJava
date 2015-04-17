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
		var2.fetchToLocal();
		if(var2.isOnCloud()) {
			for(double d : var2.getData()) {
				System.out.println(d);
			}
		}
		
		CloudVar var3 = new CloudVar("var456");
		System.out.println(var3.isOnCloud());

	}

}
