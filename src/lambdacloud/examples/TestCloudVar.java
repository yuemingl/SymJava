package lambdacloud.examples;

import lambdacloud.core.CloudConfig;
import lambdacloud.core.CSD;

public class TestCloudVar {

	public static void main(String[] args) {
		CloudConfig.setTarget("server");
		
		// Store var123 to the cloud
		double[] data = {1,2,3,4,5,6};
		CSD var = new CSD("var123").init(data);
		var.storeToCloud();
		
		// The variable var123 should be on the cloud now. 
		// We fetch it to local and print out the numbers
		CSD var2 = new CSD("var123");
		var2.fetchToLocal();
		if(var2.isOnCloud()) {
			for(double d : var2.getData()) {
				System.out.println(d);
			}
		}
		
		// This variable should not be on the cloud
		CSD var3 = new CSD("out123");
		var3.fetchToLocal();
		if(var3.isOnCloud()) {
			for(double d : var3.getData()) {
				System.out.println(d);
			}
		}
	}

}
