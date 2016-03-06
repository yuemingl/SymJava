package lambdacloud.test;

import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudSD;

public class TestCloudSD {

	public static void main(String[] args) {
		//test1();
		test2();
	}
	
	public static void test1() {
		CloudConfig.setGlobalTarget("job_aws.conf");
		
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
	
	public static void test2() {
		CloudSD v1 = new CloudSD("csd://127.0.0.1:8322/var1");
		v1.init(new double[]{1,2,3,4,5,6,7,8,9,10,11});
		System.out.println(v1.getName());
		System.out.println(v1.getFullName());
		v1.storeToCloud();
		v1.fetchToLocal();
		for(double d : v1.getData())
			System.out.println(d);
	}

}
