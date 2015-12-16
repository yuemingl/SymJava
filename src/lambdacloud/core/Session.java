package lambdacloud.core;

import java.util.Map;

import lambdacloud.core.graph.GraphBuilder;
import lambdacloud.core.graph.Node;
import symjava.symbolic.Expr;
import symjava.symbolic.utils.Utils;

public class Session {
	public void run(Expr expr, CloudSD input, CloudSD output) {
		CloudConfig.setGlobalTarget("job_local.conf");
		CloudFunc f = new CloudFunc(Utils.extractSymbols(expr).toArray(new Expr[]{}), expr);
		f.apply(output, input);
	}
	
	public double run(Expr expr, Map<String, Double> dict) {
		Node n = GraphBuilder.build(expr);
		return run(n, dict);
	}
	
	public double run(Node root, Map<String, Double> dict) {
		double[] args = new double[root.args.size()];
		for(int i=0; i<root.args.size(); i++) {
			Double d = dict.get(root.args.get(i).toString());
			if(d == null) {
				Node child = root.children.get(root.args.get(i).toString());
				args[i] = run(child, dict);
			} else {
				args[i] = d;
			}
		}
		return root.func.apply(args);
	}
}
