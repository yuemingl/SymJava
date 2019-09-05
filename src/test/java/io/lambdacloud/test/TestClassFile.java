package io.lambdacloud.test;

import io.lambdacloud.core.CloudConfig;
import io.lambdacloud.core.CloudFunc;
import io.lambdacloud.core.CloudSD;
import io.lambdacloud.symjava.bytecode.BytecodeFuncImp1;

public class TestClassFile {

	public static void main(String[] args) {
		CloudConfig.setGlobalConfig("job_aws.conf");
		
		CloudFunc f = new CloudFunc(BytecodeFuncImp1.class);
		
		CloudSD input = new CloudSD("input").init(new double[]{3, 4});
		
		CloudSD output = new CloudSD("output").resize(1);
		f.apply(output, input);
		
		if(output.fetch()) {
			for(double d : output.getData()) {
				System.out.println(d);
			}
		}
	}
}
