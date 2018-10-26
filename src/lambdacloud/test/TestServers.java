package lambdacloud.test;

import static lambdacloud.core.LambdaCloud.CPU;
import static symjava.math.SymMath.sqrt;
import static symjava.symbolic.Symbol.x;
import static symjava.symbolic.Symbol.y;
import static symjava.symbolic.Symbol.z;

import java.util.HashMap;
import java.util.Map;

import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudSD;
import lambdacloud.core.Session;
import symjava.symbolic.Expr;

public class TestServers {

	public static void main(String[] args) {
		test_multiple_inputs_for_bytecodefunc();
		test();
	}
	
	public static void test_multiple_inputs_for_bytecodefunc() {
		CloudConfig config = CloudConfig.setGlobalConfig("job_local.conf");
		
		Expr sum = x + y + z;
		CloudFunc f = new CloudFunc(config, sum, new Expr[]{x,y,z});
		
		//CloudSD input1 = new CloudSD("input1").init(new double[]{1, 2});
		CloudSD input1 = new CloudSD("csd://127.0.0.1:8323/input11").init(new double[]{1, 2});
		CloudSD input2 = new CloudSD("input2").init(new double[]{3});
		CloudSD output = new CloudSD("output").resize(1);
		
		f.apply(output, input1, input2);
		
		if(output.fetch()) {
			for(double d : output.getData()) {
				System.out.println(d);
			}
		}
	}
	
	public static void test() {
		//TODO4
		//make sure the expression can be run on different servers
		Expr sum = CPU(x*x) + CPU(y*y);
		Expr expr = CPU(sqrt(sum));
		
		System.out.println(expr);
		
		Session sess = new Session();
		Map<String, Double> dict = new HashMap<String, Double>();
		dict.put(x.toString(), 3.0);
		dict.put(y.toString(), 4.0);
		
		CloudSD output = sess.runOpt(expr, dict);
		if(output.fetch()) {
			for(double d : output.getData()) {
				System.out.println(d);
			}
		}
	}

}
