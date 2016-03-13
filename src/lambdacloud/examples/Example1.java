package lambdacloud.examples;

import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudSD;

/**
 * This example shows how to use class CloudSD (Cloud Shared Data)
 * to push (store) your data to the cloud and fetch it to the local.
 *
 */
public class Example1 {

	public static void main(String[] args) {
		// Set your secure configure file. 
		// You can register an account and download the config file from 
		// http://lambdacloud.io
		CloudConfig.setGlobalTarget("job_local.conf");
		
		// Store myData to the cloud, which is initialized by an array of double numbers.
		double[] data = {1,2,3,4,5,6};
		CloudSD myData = new CloudSD("myData1").init(data);
		if(myData.push())
			System.out.println("myData1 is on the cloud now.");
		
		// We can fetch it to our local machine at some where else
		// as long as we create an instance of CloudSD and provide
		// the name myData1
		anotherFunction();
	}
	
	public static void anotherFunction() {
		CloudSD data = new CloudSD("myData1");
		if(data.fetch()) {
			for(double d : data.getData()) {
				System.out.println(d);
			}
		}
	}
}
