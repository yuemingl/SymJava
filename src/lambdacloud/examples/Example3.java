package lambdacloud.examples;

import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudSD;
import lambdacloud.core.lang.LCBuilder;
import lambdacloud.core.lang.LCDouble;
import lambdacloud.core.lang.LCDoubleArray;
import lambdacloud.core.lang.LCInt;
import symjava.relational.Lt;

/**
 * This example shows how to use LCBuilder to create a function
 * that sums up the numbers from the arguments. We also use local
 * configuration for cloud shared data (CloudSD) and cloud function
 * (CloudFunc)
 *
 */
public class Example3 {

	public static void main(String[] args) {
		CloudConfig config = CloudConfig.instance("job_rackspace.conf");
		//config.useClient(config.getClientByIndex(2));
		System.out.println("Current host: "+config.currentClient().host);
		
		LCBuilder task = new LCBuilder(config);

		LCDoubleArray argData = new LCDoubleArray("argData"); //double[] argData;
		LCInt i = new LCInt("i"); //int i=0;
		LCDouble sum = new LCDouble("sum"); //double sum=0;
		
		/**
		 * double apply(double[] argData) {
		 * 	for(i=0; i<argData.length; i++) {
		 * 		sum = sum + argData[i];
		 * 	}
		 * 	return sum;
		 * }
		 */
		task.For(i.assign(0), Lt.apply(i, argData.getLength()), i.inc())
			.appendBody(sum.assign( sum + argData[i] ));
		task.Return(sum);
		CloudFunc func = task.build(argData);
		System.out.println(task);

		CloudSD myOutput = new CloudSD(config, "myOutput").resize(1);
		CloudSD myData = new CloudSD(config, "myData").init(new double[] {2,2,3,3,4,4});
		myData.push();
		
		// Evaluating on the cloud server
		func.apply(myOutput, myData);

		if(myOutput.fetch()) {
			for(double d : myOutput.getData()) {
				System.out.println(d);
			}
		}
	}

}
