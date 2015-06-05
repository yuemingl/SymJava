package lambdacloud.core;

import java.util.ArrayList;
import java.util.List;

import symjava.symbolic.Expr;
import symjava.symbolic.Symbol;

/**
 * Lambda Cloud instruction builder
 *
 */
public class LC {
	CloudConfig config;
	List<Expr> stmts = new ArrayList<Expr>();

	public LC(CloudConfig config) {
		this.config = config;
	}
	
	public LC(String configFile) {
		
	}
	
	public CloudLoop forLoop(Expr initExpr, Expr conditionExpr, Expr incrementExpr) {
		CloudLoop cl = new CloudLoop(initExpr, conditionExpr, incrementExpr);
		stmts.add(cl);
		return cl;
	}
	
	public CloudLoop whileLoop(Expr conditionExpr) {
		return new CloudLoop(conditionExpr);
	}
	
	public CloudIf If(Expr condition) {
		return new CloudIf(condition);
	}
	public LC append(Expr expr) {
		stmts.add(expr);
		return this;
	}
	
	public CloudSharedVar globalVar(String name) {
		return new CloudSharedVar(name);
	}

	public CloudVar localVar(String name) {
		return new CloudVar(name);
	}
	
	public void run() {
		
	}
}
