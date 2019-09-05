package io.lambdacloud.examples;

import io.lambdacloud.core.CloudConfig;
import io.lambdacloud.core.CloudFunc;
import io.lambdacloud.core.CloudSD;
import io.lambdacloud.core.lang.LCBuilder;
import io.lambdacloud.core.lang.LCDouble;
import io.lambdacloud.core.lang.LCDoubleArray;
import io.lambdacloud.core.lang.LCInt;
import io.lambdacloud.symjava.relational.Lt;

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
		
		LCBuilder lcb = new LCBuilder(config);

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
		lcb.For(i.assign(0), Lt.apply(i, args.getLength()), i.inc())
			.appendBody(sum.assign( sum + args[i] ));
		lcb.Return(sum);
		
		CloudFunc func = lcb.build(args); //Pass args as arguments and build the function
		System.out.println(lcb);

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
