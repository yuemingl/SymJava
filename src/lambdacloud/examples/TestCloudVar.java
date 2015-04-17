package lambdacloud.examples;

import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudVar;

public class TestCloudVar {

	public static void main(String[] args) {
		CloudConfig.setTarget("server");
		
		// Store var123 to the cloud
		double[] data = {1,2,3,4,5,6};
		CloudVar var = new CloudVar("var123").init(data);
		var.storeToCloud();
		
		// The variable var123 should be on the cloud now. 
		// We fetch it to local and print out the numbers
		CloudVar var2 = new CloudVar("var123");
		var2.fetchToLocal();
		if(var2.isOnCloud()) {
			for(double d : var2.getData()) {
				System.out.println(d);
			}
		}
		
		// This variable should not be on the cloud
		CloudVar var3 = new CloudVar("var456");
		System.out.println(var3.isOnCloud());

	}

}
