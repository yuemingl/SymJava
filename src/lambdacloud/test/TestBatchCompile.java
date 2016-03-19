package lambdacloud.test;

import static symjava.symbolic.Symbol.x;
import static symjava.symbolic.Symbol.y;
import static symjava.symbolic.Symbol.z;
import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudSD;
import symjava.symbolic.Expr;

public class TestBatchCompile {

	public static void main(String[] args) {
		test(null); //local
		test(new CloudConfig("job_local.conf"));
	}
	
	public static void test(CloudConfig config) {
		CloudFunc fun = new CloudFunc(config,
				new Expr[] 
						{ z,
						  y*y,
						  x+y+z
						}, 
						x,y,z);
		CloudSD output = new CloudSD();
		CloudSD input = new CloudSD().init(new double[]{1,2,3});
		fun.apply(output, input);
		if(output.fetch())
			System.out.println(output);
	}

}
