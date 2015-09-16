package lambdacloud.examples;

import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudSD;
import lambdacloud.core.lang.LCVar;
import symjava.bytecode.BytecodeFunc;
import symjava.symbolic.Expr;
import symjava.symbolic.utils.JIT;

public class ExamplePaper {

	public static void main(String[] args) {
		CloudConfig.setGlobalTarget("job_local.conf");
		LCVar x = LCVar.getDouble("x");
		LCVar y = LCVar.getDouble("y");
		Expr R = 0.127-(x*0.194/(y+0.194));
		//Derivative of expression R with respect to y
		Expr Rdy = R.diff(y); 
		//Define a cloud function
		CloudFunc fun = new CloudFunc(new LCVar[]{x, y}, Rdy);
		//Evaluate the function on the cloud by providing
		//input and output parameters
		CloudSD input = new CloudSD("input").init(
				new double[]{0.362, 0.556});
		CloudSD output = new CloudSD("output").resize(1);
		fun.apply(output, input);
		if(output.fetchToLocal()) {
			System.out.println(output.getData()[0]);
		}

		//Just-In-Time compile the symbolic expression to native code
		//and evaluate it locally to verify the result
		BytecodeFunc func = JIT.compile(new Expr[]{x,y}, Rdy);
		System.out.println(func.apply(0.362, 0.556));
	}

}
