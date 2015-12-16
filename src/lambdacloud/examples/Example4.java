package lambdacloud.examples;

import static symjava.math.SymMath.sqrt;
import static symjava.symbolic.Symbol.x;
import static symjava.symbolic.Symbol.y;

import java.util.HashMap;
import java.util.Map;

import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudSD;
import lambdacloud.core.Session;
import lambdacloud.core.graph.GraphBuilder;
import lambdacloud.core.graph.Node;
import lambdacloud.core.lang.LCDevice;
import symjava.symbolic.Expr;
import symjava.symbolic.utils.AddList;

public class Example4 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//CloudConfig.setGlobalTarget("job_local.conf");
		
		Expr sum = x*x + y*y;
		Expr[] ops = sum.args();
		for(int i=0; i<ops.length; i++) {
			ops[i].runOn(new LCDevice("/cpu:"+i));
		};

		LCDevice cpu1 = new LCDevice("/cpu:0");
		LCDevice cpu2 = new LCDevice("/cpu:1");
		Expr sum2 = (x*x).runOn(cpu1) + (y*y).runOn(cpu2);
		
		Expr expr = sqrt(sum);
		Expr expr2 = sqrt(sum2);
		System.out.println(expr);
		System.out.println(expr2);
		
		

		
		
		
		
		Session sess = new Session();
		Map<String, Double> dict = new HashMap<String, Double>();
		dict.put(x.toString(), 3.0);
		dict.put(y.toString(), 4.0);
		
		double rlt = sess.run(expr, dict);
		System.out.println(rlt);
		
//		CloudSD input = new CloudSD("input").init(new double[]{3, 4});
//		CloudSD output = new CloudSD("output").resize(1);
//		//sess.run(expr, input, output);
//		sess.run(expr2, input, output);
//		if(output.fetchToLocal()) {
//			for(double d : output.getData()) {
//				System.out.println(d);
//			}
//		}
	}

}
