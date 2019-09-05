package io.lambdacloud.test;

import io.lambdacloud.core.CloudConfig;
import io.lambdacloud.core.CloudFunc;
import io.lambdacloud.core.CloudSD;
import io.lambdacloud.core.lang.LCVar;
import io.lambdacloud.symjava.symbolic.Expr;

/**
		for(int i=0; i<10; i++) {
			f.apply(output, input);
			Expr update = input + 1.0*output;
			input = update; //Cast update to type CloudSD
		}

 *
 */
public class TestBatchUpdate {

	public static void main(String[] args) {
		test(null);
		test(new CloudConfig("job_local.conf"));
	}
	
	public static void test(CloudConfig config) {
		
		LCVar x = LCVar.getDouble("x");
		LCVar y = LCVar.getDouble("y");

		Expr[] exprs = new Expr[] {
			x + y,
			x - y
		};
		CloudFunc f = new CloudFunc(config, "a_vector_function", exprs, new LCVar[]{x, y});
		
		CloudSD input = new CloudSD("input").init(new double[]{2, 1});
		CloudSD output = new CloudSD("output");
		
		long begin = System.currentTimeMillis();
		//f.apply(output, input);
		// Call apply in every iteration
		for(int i=0; i<10; i++) {
			f.apply(output, input);
			output.fetch(); //This fetch can be removed as long as the clone is removed from class Multiply
			Expr update = input + 1.0*output;
			//The output here is a different object from the output due to clone in class Multiply
			input = update; //Cast update to type CloudSD
			if(input.fetch()) {
				for(double d : input.getData())
					System.out.println(d);
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("Time: "+((end-begin)/1000.0));
		if(output.fetch()) {
			for(double d : output.getData()) {
				System.out.println(d);
			}
		}
	}
	
}

