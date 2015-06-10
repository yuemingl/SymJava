package lambdacloud.test;

import static symjava.symbolic.Symbol.x;
import static symjava.symbolic.Symbol.y;
import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudDouble;
import lambdacloud.core.CloudStatements;
import lambdacloud.core.CloudVar;
import lambdacloud.core.operators.OPReturn;
import symjava.symbolic.Expr;

public class TestCloudStatements {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		CloudConfig.setTarget("server");
		
		CloudVar i = new CloudDouble("i");
		CloudVar j = new CloudDouble("j");
		CloudVar ret = new CloudDouble("ret");
		
		CloudStatements s = new CloudStatements();
		s.append(i.assign(1)); //i=1
		s.append(j.assign(x*y)); //j=x*y
		s.append(ret.assign(i+j)); //result=i+j
		s.append(new OPReturn(ret));
		System.out.println(s);
		
		double t = CompileUtils.compile(s, new Expr[]{x, y}).apply(3,4);
		System.out.println(t);
	}

}
