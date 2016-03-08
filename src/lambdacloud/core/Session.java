package lambdacloud.core;

import java.util.Map;

import lambdacloud.core.graph.GraphBuilder;
import lambdacloud.core.graph.Node;
import symjava.symbolic.Expr;
import symjava.symbolic.Matrix;
import symjava.symbolic.Vector;
import symjava.symbolic.utils.Utils;

public class Session {
	public double run(Expr expr, Map<String, Double> dict) {
		CloudConfig.setGlobalTarget("job_local.conf");
		Node n = GraphBuilder.build(expr);
		return run(n, dict);
	}
	
	public CloudSD runVec(Expr expr, Map<String, double[]> dict) {
		CloudConfig.setGlobalTarget("job_local.conf");
		Node n = GraphBuilder.build(expr);
		return runVec(n, dict);
	}
	
	/**
	 * Test for scalar
	 * The optimization is suitable for matrix and vectors
	 * @param expr
	 * @param dict
	 * @return
	 */
	public CloudSD runOpt(Expr expr, Map<String, Double> dict) {
		CloudConfig.setGlobalTarget("job_local.conf");
		Node n = GraphBuilder.build(expr);
		return runOpt(n, dict);
	}
	
	public CloudSD runVec(Node root, Map<String, double[]> dict) {
		int nArgs = root.args.size();
		CloudSD[] inputs = new CloudSD[nArgs];
		for(int i=0; i<nArgs; i++) {
			Expr arg = root.args.get(i);
			double[] d = dict.get(arg.toString());
			if(d == null && arg.getParent() != null) {
				d = dict.get(arg.getParent().toString());
				if(d != null) {
					if(arg instanceof Matrix) {
						//extract sub-matrix from parent matrix
						Matrix m = (Matrix)arg;
						Matrix p = (Matrix)arg.getParent();
						Jama.Matrix mat = new Jama.Matrix(d, p.nRow);
						d = mat.getMatrix(m.nRowStart, m.nRowStart+m.nRow-1, m.nColStart, m.nColStart+m.nCol-1).getColumnPackedCopy();
					} else if (arg instanceof Vector) {
						//extract sub-vector from parent vector
						Vector m = (Vector)arg;
						Vector p = (Vector)arg.getParent();
						Jama.Matrix mat = new Jama.Matrix(d, p.nDim);
						d = mat.getMatrix(m.nStart, m.nStart+m.nDim-1, 0, 0).getColumnPackedCopy();
					}
				}
			}
			if(d == null) {
				Node child = root.children.get(root.args.get(i).toString());
				CloudSD ret = runVec(child, dict);
				inputs[i] = ret;
			} else {
				inputs[i] = new CloudSD(arg.toString()).init(d);
			}
		}
		CloudSD output = new CloudSD("");//"output").resize(4); //TODO

		System.out.print(">>Session eval: "+root+"; args:\n[");
		for(int i=0; i<inputs.length; i++) {
			System.out.println("\t"+inputs[i]);
		}
		System.out.print("]");
		root.cfunc.apply(output, inputs);
		if(output.fetchToLocal()) {
			System.out.print("Return: [");
			for(double d : output.getData()) {
				System.out.print(d+" ");
			}
			System.out.println("]");
		}
		
		return output;
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

	/**
	 * The run_opt method does not fetch a CloudSD from another server.
	 * That is to say, only the name is passed to apply() function.
	 * This avoid to fetch data from different server every time.
	 * 
	 * @param root
	 * @param dict
	 * @return
	 */
	public CloudSD runOpt(Node root, Map<String, Double> dict) {
		CloudSD[] args = new CloudSD[root.args.size()];
		for(int i=0; i<root.args.size(); i++) {
			Double d = dict.get(root.args.get(i).toString());
			if(d == null) {
				Node child = root.children.get(root.args.get(i).toString());
				args[i] = runOpt(child, dict);
			} else {
				//for arguments in the dict will be changed to a CloudSD
				args[i] = new CloudSD(root.args.get(i).toString()).init(new double[]{d});
			}
		}
		CloudSD output = new CloudSD("").resize(1);
		System.out.print(">>Session eval: "+root+"; args:\n[");
		//Utils.joinLabels(args, ", ");
		for(int i=0; i<args.length; i++) {
			System.out.println("\t"+args[i]);
		}
		System.out.println("]");
		root.cfunc.apply(output, args);
		return output;
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
