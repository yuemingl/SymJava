package lambdacloud.examples;

import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudLocalVar;
import lambdacloud.core.CloudStatements;
import lambdacloud.core.CloudVar;
import static symjava.symbolic.Symbol.*;

public class TestCloudStatements {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		CloudConfig.setTarget("server");
		
		CloudLocalVar i = new CloudLocalVar("i");
		CloudLocalVar j = new CloudLocalVar("j");
		CloudVar result = new CloudVar("TestCloudStatements.result");
		CloudVar input = new CloudVar("TestCloudStatements.input");
		input.init(new double[]{3.0,4.0});
		input.storeToCloud();
		
		CloudStatements s = new CloudStatements();
		s.append(i.assign(1)); //i=1
		s.append(j.assign(x*y)); //j=x*y
		s.append(result.assign(i+j)); //result=i+j
		
		s.apply(input);
		
		result.fetchToLocal();
		System.out.println(result.get(0));
	}

}
