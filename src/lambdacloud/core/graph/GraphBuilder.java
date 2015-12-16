package lambdacloud.core.graph;

public class GraphBuilder {
	public static Node build(Expr expr) {
		return helper(expr);
	}

	public static Node helper(Expr expr) {
		Expr[] args = expr.args();
		//stop?
		Node ret = new Node();
		ret.expr = expr;

		Symbols ss = new Symbols("__x");
		int idx = 0;
		for(int i=0; i<args.length; i++) {
			Node n = helper(args[i]);
			if(n.isDevice()) {
				Symbol arg = ss.get(idx++);
				expr.setArg(i, arg);
				ret.args.add(arg);
				ret.children.add(n);
			}
		}
		return ret;
	}
}
