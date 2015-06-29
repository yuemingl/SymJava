package lambdacloud.test;

import lambdacloud.core.CloudConfig;
import lambdacloud.core.lang.LCDouble;
import lambdacloud.core.lang.LCReturn;
import lambdacloud.core.lang.LCStatements;
import lambdacloud.core.lang.LCVar;

public class TestLCStatements {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		CloudConfig.setGlobalTarget("job_aws.conf");
		
		LCVar x = new LCDouble("x");
		LCVar y = new LCDouble("y");
		
		LCVar i = new LCDouble("i");
		LCVar j = new LCDouble("j");
		LCVar ret = new LCDouble("ret");
		
		LCStatements s = new LCStatements();
		s.append(i.assign(1));       //i = 1;
		s.append(j.assign(x*y));     //j = x*y;
		s.append(ret.assign(i+j));   //result = i+j;
		s.append(new LCReturn(ret)); //return result;
		System.out.println(s);
		
		double t = CompileUtils.compile(s, new LCVar[]{x, y}).apply(3,4);
		System.out.println(t);
	}

}
