package lambdacloud.core.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudSD;
import symjava.bytecode.BytecodeFunc;
import symjava.symbolic.Expr;

public class Node {
	public Expr expr;
	public List<Expr> args = new ArrayList<Expr>();
	public BytecodeFunc func;
	public CloudFunc cfunc;
	public Map<String, Node> children = new HashMap<String, Node>();
	public boolean isDevice() {
		return expr.getDevice()!=null;
	}
	public Node() {
		
	}
}
