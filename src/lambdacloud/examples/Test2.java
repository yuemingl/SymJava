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
		CloudConfig.setTarget("local");
		
		Expr[] exprs = new Expr[] {
			x + y,
			x - y
		};
		CloudFunc f = new CloudFunc("a_vector_function", new Expr[]{x, y}, exprs);
		
		CloudVar input = new CloudVar("input").init(new double[]{1, 2});
		CloudVar output = new CloudVar("output").resize(2);
		
		long begin = System.currentTimeMillis();
		//f.apply(output, input);
		for(int i=0; i<10; i++) {
			f.apply(output, input);
			Expr update = input + 0.1*output;
			input = update; //Cast update to type CloudVar
		}
		long end = System.currentTimeMillis();
		System.out.println("Time: "+((end-begin)/1000.0));
		for(double d : output.fetchToLocal()) {
			System.out.println(d);
		}
	}
}

