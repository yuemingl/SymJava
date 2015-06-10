package lambdacloud.test;

import static symjava.symbolic.Symbol.x;
import static symjava.symbolic.Symbol.y;
import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.CSD;
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
		
		CSD input = new CSD("input").init(new double[]{2, 1});
		CSD output = new CSD("output").resize(2);
		
		long begin = System.currentTimeMillis();
		//f.apply(output, input);
		for(int i=0; i<10; i++) {
			f.apply(output, input);
			Expr update = input + 1.0*output;
			input = update; //Cast update to type CloudVar
			if(input.fetchToLocal()) {
				for(double d : input.getData())
					System.out.println(d);
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("Time: "+((end-begin)/1000.0));
		if(output.fetchToLocal()) {
			for(double d : output.getData()) {
				System.out.println(d);
			}
		}
	}
	
}

