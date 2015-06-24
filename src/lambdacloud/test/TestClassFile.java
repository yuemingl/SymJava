package lambdacloud.test;

import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudSD;
import symjava.bytecode.BytecodeFunc;

public class TestClassFile implements BytecodeFunc {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CloudConfig.setTarget("local");
		
		
		CloudFunc f = new CloudFunc(TestClassFile.class);
		
		CloudSD input = new CloudSD("input").init(new double[]{3, 4});
		
		CloudSD output = new CloudSD("output").resize(1);
		f.apply(output, input);
		
		if(output.fetchToLocal()) {
			for(double d : output.getData()) {
				System.out.println(d);
			}
		}
	}

	@Override
	public double apply(double... args) {
		return args[0]+args[1];
	}

}
