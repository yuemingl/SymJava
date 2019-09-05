package io.lambdacloud.core.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.lambdacloud.core.CloudFunc;
import io.lambdacloud.core.CloudSD;
import io.lambdacloud.symjava.bytecode.BytecodeFunc;
import io.lambdacloud.symjava.symbolic.Expr;

public class Node {
	public Expr expr;
	public List<Expr> args = new ArrayList<Expr>(); //arguments of expr; added at compile time
	
	public BytecodeFunc func; //for test purpose
	
	public CloudFunc cfunc; // All the server info are stored in CloudFunc
	public Map<String, Node> children = new HashMap<String, Node>(); //Map of the child nodes if an argument is a node
	
	public boolean isDevice() {
		return expr.getDevice()!=null;
	}
	
	public Node(Expr expr) {
		this.expr = expr;
	}
	
	public String toString() {
		return expr.toString();
	}
}
