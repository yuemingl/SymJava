package lambdacloud.examples;

import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudSD;
import lambdacloud.core.lang.LCBuilder;
import lambdacloud.core.lang.LCVar;
import symjava.relational.Lt;

/**
 * This is a useful example 
 *
 */
public class Example3 {

	public static void main(String[] args) {
		LCBuilder lcb = new LCBuilder("local");

		double[] data = {1,2,3,4,5,6};
		CloudSD myData = new CloudSD("myData").init(data);
		myData.storeToCloud();
		
		LCVar i = lcb.declareInt("i"); //int i=0;
		LCVar sum = lcb.declareDouble("sum"); //double sum=0;
		
		/**
		 * for(i=0; i<6; i=i+1) {
		 * 	sum = sum + i;
		 * }
		 */
		lcb.For(i.assign(0), Lt.apply(i, 11), i.assign(i+1))
			.appendBody(sum.assign(sum+i));
		
		lcb.Return(sum); //return sum;
		
		CloudFunc func = lcb.build();
		CloudSD myOutput = new CloudSD("myOutput").resize(1);
		func.apply(myOutput, myData);
		if(myOutput.fetchToLocal()) {
			for(double d : myOutput.getData()) {
				System.out.println(d);
			}
		}
	}

}
