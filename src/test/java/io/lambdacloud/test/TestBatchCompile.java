package io.lambdacloud.test;

import static io.lambdacloud.symjava.symbolic.Symbol.x;
import static io.lambdacloud.symjava.symbolic.Symbol.y;
import static io.lambdacloud.symjava.symbolic.Symbol.z;
import io.lambdacloud.core.CloudConfig;
import io.lambdacloud.core.CloudFunc;
import io.lambdacloud.core.CloudSD;
import io.lambdacloud.symjava.symbolic.Expr;

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
