package lambdacloud.core.graph;

import java.util.ArrayList;

import lambdacloud.core.CloudSD;
import symjava.bytecode.BytecodeFunc;
import symjava.symbolic.Expr;

public class Node {
	public Expr expr;
	public List<Expr> args = ArrayList<Expr>();
	public BytecodeFunc func;
	public List<Node> children = ArrayList<Node>();
	public boolean isDevice() {
		return expr.getDevice()!=null;
	}
	public Node() {

	}
}
