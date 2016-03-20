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
 * that sums up the numbers from the arguments. We also demo using local and cloud
 * configuration for cloud shared data (CloudSD) and cloud function
 * (CloudFunc)
 *
 */
public class Example3 {

	public static void main(String[] args) {
		example(null); // Run locally
		example(new CloudConfig("job_local.conf")); // Run on cloud
	}
	
	public static void example(CloudConfig config) {
		
		LCBuilder task = new LCBuilder(config);

		//Declare variables
		LCDoubleArray args    = new LCDoubleArray("args"); //double[] args;
		LCInt i               = new LCInt("i");            //int i=0;
		LCDouble sum          = new LCDouble("sum");       //double sum=0;
		
		/**
		 * double apply( double[] args ) {
		 * 	for(i=0; i<argData.length; i++) {
		 * 		sum = sum + argData[i];
		 * 	}
		 * 	return sum;
		 * }
		 */
		task.For(i.assign(0), Lt.apply(i, args.getLength()), i.inc())
			.appendBody(sum.assign( sum + args[i] ));
		task.Return(sum);
		
		CloudFunc func = task.build(args); //Pass args as arguments and build the function
		System.out.println(task);

		CloudSD output = new CloudSD();
		CloudSD myData = new CloudSD("myData").init(new double[] {2,2,3,3,4,4});
		
		//Call the compiled function
		func.apply(output, myData);

		if(output.fetch()) {
			for(double d : output.getData()) {
				System.out.println(d);
			}
		}
	}

}
