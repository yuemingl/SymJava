package lambdacloud.test;

import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudSD;

public class TestCSD {

	public static void main(String[] args) {
		CloudConfig.setTarget("server");
		
		// Store var123 to the cloud
		double[] data = {1,2,3,4,5,6};
		CloudSD var = new CloudSD("var123").init(data);
		var.storeToCloud();
		
		// The variable var123 should be on the cloud now. 
		// We fetch it to local and print out the numbers
		CloudSD var2 = new CloudSD("var123");
		var2.fetchToLocal();
		if(var2.isOnCloud()) {
			for(double d : var2.getData()) {
				System.out.println(d);
			}
		}
		
		// This variable should not be on the cloud
		CloudSD var3 = new CloudSD("out123");
		var3.fetchToLocal();
		if(var3.isOnCloud()) {
			for(double d : var3.getData()) {
				System.out.println(d);
			}
		}
	}

}
