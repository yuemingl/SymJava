package lambdacloud.examples;

import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudSD;
import lambdacloud.core.lang.LCArray;
import lambdacloud.core.lang.LCBuilder;
import lambdacloud.core.lang.LCDouble;
import lambdacloud.core.lang.LCDoubleArray;
import lambdacloud.core.lang.LCInt;
import lambdacloud.core.lang.LCVar;
import symjava.relational.Lt;

/**
 * This example shows how to use LCBuilder to create a function
 * that sums up the numbers in the argument 
 *
 */
public class Example3 {

	public static void main(String[] args) {
		LCBuilder lcb = new LCBuilder("server");

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
		lcb.For(i.assign(0), Lt.apply(i, argData.getLength()), i.inc())
			.appendBody(sum.assign( sum + argData[i] ));
		lcb.Return(sum);
		CloudFunc func = lcb.build(argData);
		System.out.println(lcb);

		CloudSD myOutput = new CloudSD("myOutput").resize(1);
		CloudSD myData = new CloudSD("myData").init(new double[] {2,2,3,3,4,4});
		myData.storeToCloud();
		
		// Evaluating on the cloud server
		func.apply(myOutput, myData);

		if(myOutput.fetchToLocal()) {
			for(double d : myOutput.getData()) {
				System.out.println(d);
			}
		}
	}

}
