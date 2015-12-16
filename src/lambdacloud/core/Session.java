package lambdacloud.core;

import symjava.symbolic.Expr;
import symjava.symbolic.utils.Utils;

public class Session {
	public void run(Expr expr, CloudSD input, CloudSD output) {
		CloudConfig.setGlobalTarget("job_local.conf");
		CloudFunc f = new CloudFunc(Utils.extractSymbols(expr).toArray(new Expr[]{}), expr);
		f.apply(output, input);
	}
}
