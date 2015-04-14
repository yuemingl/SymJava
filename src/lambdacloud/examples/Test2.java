package lambdacloud.examples;

import static symjava.symbolic.Symbol.x;
import static symjava.symbolic.Symbol.y;
import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudVar;
import symjava.symbolic.Expr;

public class Test2 {

	public static void main(String[] args) {
		test();
	}
	
	public static void test() {
		CloudConfig config = new CloudConfig("local");
		
		Expr[] exprs = new Expr[] {
			x + y,
			x - y
		};
		CloudFunc f = new CloudFunc(config);
		f.compile(new Expr[]{x, y}, exprs);
		
		CloudVar input = new CloudVar(config,"input").init(new double[]{1, 2});
		CloudVar output = new CloudVar(config,"output").resize(2);
		
		long begin = System.currentTimeMillis();
		f.apply(output, input);
		long end = System.currentTimeMillis();
		System.out.println("Time: "+((end-begin)/1000.0));
		for(double d : output.fetchToLocal()) {
			System.out.println(d);
		}
		
		Expr update = x + input + 1;
		CloudVar s = 1+2;
		Expr zz = x + y;
		CloudVar ss = x + zz;
		
		//f.apply(output, update);
	}
}
