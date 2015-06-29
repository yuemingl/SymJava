package lambdacloud.examples;

import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudSD;

/**
 * This example shows how to use class CloudSD (Cloud Shared Data)
 * to store your data to LambdaCloud and to fetch it from the cloud.
 *
 */
public class Example1 {

	public static void main(String[] args) {
		// Set your secure configure file. 
		// You can register an account and download it from 
		// http://lambdacloud.io
		CloudConfig.setGlobalTarget("job_local.conf");
		
		// Store myData to the cloud, which is initialized by 
		// an array of double numbers.
		double[] data = {1,2,3,4,5,6};
		CloudSD myData = new CloudSD("myData").init(data);
		myData.storeToCloud();
		
		// The data myData should be on the cloud now. 
		// We fetch it to local machine and print out the numbers.
		// In order to fetch a named shared data, you need define
		// an instance of CloudSD with the name.
		CloudSD data2 = new CloudSD("myData");
		data2.fetchToLocal();
		if(data2.isOnCloud()) {
			for(double d : data2.getData()) {
				System.out.println(d);
			}
		}
	}
}
