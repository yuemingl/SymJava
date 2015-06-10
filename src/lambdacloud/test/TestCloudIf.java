package lambdacloud.test;

import lambdacloud.core.CloudIf;
import lambdacloud.core.CloudInt;
import lambdacloud.core.CloudVar;
import symjava.relational.Gt;
import symjava.relational.Lt;

public class TestCloudIf {
	public static void main(String[] args) {
		CloudVar x = new CloudInt("x");
		CloudIf ifa = new CloudIf(Gt.apply(x, 100));
		ifa.appendTrue(x.assign(x+100));
		ifa.appendTrue(x.assign(x+1));
		ifa.appendFalse(x.assign(x*100));
		System.out.println(ifa);
		CompileUtils.compile(ifa);
		
	}
}
