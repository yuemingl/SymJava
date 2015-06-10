package lambdacloud.core;

import java.util.ArrayList;
import java.util.List;

import symjava.symbolic.Expr;

public class CloudStatements extends CloudBase {
	List<Expr> exprList = new ArrayList<Expr>();
	
	public CloudStatements append(Expr expr) {
		exprList.add(expr);
		return this;
	}
	
	protected void initLabel() {
		StringBuilder sb = new StringBuilder();
		for(Expr e : exprList) {
			sb.append(e).append("\n");
		}
		this.label = sb.toString();		
	}
	
	public void apply(CSD ...inputs) {
		
	}

	@Override
	public Expr[] args() {
		return this.exprList.toArray(new Expr[0]);
	}
}
