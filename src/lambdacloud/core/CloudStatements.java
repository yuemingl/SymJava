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
	
	public void apply(CloudSharedVar ...inputs) {
		
	}
}
