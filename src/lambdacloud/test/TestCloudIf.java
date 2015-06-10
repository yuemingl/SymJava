package lambdacloud.test;

import lambdacloud.core.CloudDouble;
import lambdacloud.core.CloudIf;
import lambdacloud.core.CloudStatements;
import lambdacloud.core.CloudVar;
import lambdacloud.core.operators.OPReturn;
import symjava.bytecode.BytecodeFunc;
import symjava.relational.Gt;

public class TestCloudIf {
	public static void main(String[] args) {
		CloudVar x = new CloudDouble("x");
		CloudIf ifa = new CloudIf(Gt.apply(x, 100));
		ifa.appendTrue(x.assign(x+100));
		ifa.appendTrue(x.assign(x+1));
		ifa.appendFalse(x.assign(x*100));
		//System.out.println(ifa);
		CloudStatements s = new CloudStatements();
		s.append(x.assign(101.0));
		//s.append(x.assign(10.0));
		s.append(ifa);
		s.append(new OPReturn(x));
		System.out.println(s);
		BytecodeFunc f = CompileUtils.compile(s);
		System.out.println(f.apply());
	}
}
