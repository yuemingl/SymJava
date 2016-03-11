package lambdacloud.test;

import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudSD;
import lambdacloud.core.lang.LCVar;
import symjava.symbolic.Expr;

/**
		for(int i=0; i<10; i++) {
			f.apply(output, input);
			Expr update = input + 1.0*output;
			input = update; //Cast update to type CloudSD
		}

 *
 */
public class Test2 {

	public static void main(String[] args) {
		test();
	}
	
	public static void test() {
		CloudConfig.setGlobalTarget("job_aws.conf");
		
		LCVar x = LCVar.getDouble("x");
		LCVar y = LCVar.getDouble("y");

		Expr[] exprs = new Expr[] {
			x + y,
			x - y
		};
		CloudFunc f = new CloudFunc("a_vector_function", new LCVar[]{x, y}, exprs);
		
		CloudSD input = new CloudSD("input").init(new double[]{2, 1});
		CloudSD output = new CloudSD("output").resize(2);
		
		long begin = System.currentTimeMillis();
		//f.apply(output, input);
		// Call apply in every iteration
		for(int i=0; i<10; i++) {
			f.apply(output, input);
			Expr update = input + 1.0*output;
			input = update; //Cast update to type CloudSD
			if(input.fetchToLocal()) {
				for(double d : input.getData())
					System.out.println(d);
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("Time: "+((end-begin)/1000.0));
		if(output.fetchToLocal()) {
			for(double d : output.getData()) {
				System.out.println(d);
			}
		}
	}
	
}

