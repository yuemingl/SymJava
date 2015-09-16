package lambdacloud.examples;

import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudSD;
import symjava.bytecode.BytecodeFunc;
import symjava.symbolic.Expr;
import symjava.symbolic.utils.JIT;
import static symjava.symbolic.Symbol.x;
import static symjava.symbolic.Symbol.y;

public class ExamplePaper {

	public static void main(String[] args) {
		CloudConfig.setGlobalTarget("job_local.conf");
		
		Expr R = 0.127-(x*0.194/(y+0.194));
		//Derivative of expression R with respect to y
		Expr Rdy = R.diff(y); 
		
		//Define a function on the cloud
		CloudFunc fun = new CloudFunc(new Expr[]{x, y}, Rdy);
		
		//Evaluate the function on the cloud by providing
		//input and output parameters
		CloudSD input = new CloudSD("input").init(
				new double[]{0.362, 0.556});
		CloudSD output = new CloudSD("output");
		fun.apply(output, input);
		
		//Fetch the evaluation result to local
		if(output.fetchToLocal()) {
			System.out.println(output.getData(0));
		}

		//Just-In-Time compile the symbolic expression to native code
		//and evaluate it locally to verify the result
		BytecodeFunc func = JIT.compile(new Expr[]{x,y}, Rdy);
		System.out.println(func.apply(0.362, 0.556));
	}

}
