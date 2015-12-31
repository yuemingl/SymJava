package lambdacloud.core;

import java.util.Map;

import lambdacloud.core.graph.GraphBuilder;
import lambdacloud.core.graph.Node;
import symjava.symbolic.Expr;
import symjava.symbolic.utils.Utils;

public class Session {
	public double run(Expr expr, Map<String, Double> dict) {
		CloudConfig.setGlobalTarget("job_local.conf");
		Node n = GraphBuilder.build(expr);
		return run(n, dict);
	}
	
	/**
	 * TODO
	 * run() return a double
	 * run() could return an array of Tensor
	 * 
	 * @param root
	 * @param dict
	 * @return
	 */
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
		CloudSD input = new CloudSD("input").init(args);
		CloudSD output = new CloudSD("output").resize(1);
		root.cfunc.apply(output, input);
		if(output.fetchToLocal()) {
			for(double d : output.getData()) {
				System.out.println(d);
			}
		}
		
		return output.getData(0);
	}
	
	public double runLocal(Expr expr, Map<String, Double> dict) {
		Node n = GraphBuilder.build(expr);
		return runLocal(n, dict);
	}
	
	public double runLocal(Node root, Map<String, Double> dict) {
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
