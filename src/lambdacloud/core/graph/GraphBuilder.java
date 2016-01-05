package lambdacloud.core.graph;

import lambdacloud.core.CloudFunc;
import lambdacloud.test.CompileUtils;
import symjava.symbolic.Expr;
import symjava.symbolic.Matrix;
import symjava.symbolic.Symbol;
import symjava.symbolic.Symbols;
import symjava.symbolic.TypeInfo;
import symjava.symbolic.Expr.TYPE;
import symjava.symbolic.Vector;
import symjava.symbolic.utils.JIT;
import symjava.symbolic.utils.Utils;

public class GraphBuilder {
	public static Node build(Expr expr) {
		Node n = helper(expr);
		compile(n);
		return n;
	}
	
	protected static void compile(Node root) {
		for(Node n : root.children.values()) {
			compile(n);
		}
		//no return
		//root.func = CompileUtils.compile(null, root.expr, Utils.extractSymbols(root.expr).toArray(new Expr[0]));
		root.args = Utils.extractSymbols(root.expr);
		//root.func = JIT.compile(root.expr);
		root.cfunc = new CloudFunc(root.args.toArray(new Expr[0]), root.expr);
	}
	
	public static Node helper(Expr expr) {
		Expr[] args = expr.args();
		Node ret = new Node();
		ret.expr = expr.clone();
		if(args == null || args.length == 0) 
			return ret;

		Symbols ss = new Symbols("__x");
		int idx = 0;
		for(int i=0; i<args.length; i++) {
			Node n = helper(args[i]);
			if(n != null) {
				if(n.isDevice()) {
					//
					TypeInfo ti = args[i].getTypeInfo();
					if(ti.type == TYPE.VECTOR) {
						Vector arg = new Vector("__vec_"+(idx++), ti.dim[0]);
						ret.expr.setArg(i, arg);
						//ret.args.add(arg);
						ret.children.put(arg.toString(), n);
						
					} else if(ti.type == TYPE.MATRIX) {
						Matrix arg = new Matrix("__mat_"+(idx++), ti.dim[0], ti.dim[1]);
						ret.expr.setArg(i, arg);
						//ret.args.add(arg);
						ret.children.put(arg.toString(), n);
					} else {
						Symbol arg = ss.get(idx++);
						ret.expr.setArg(i, arg);
						//ret.args.add(arg);
						ret.children.put(arg.toString(), n);
						
					}
				} else {
					ret.expr.setArg(i, n.expr);
					//ret.args.addAll(n.args);
					ret.children.putAll(n.children);
				}
			}
		}
		return ret;
	}
}
