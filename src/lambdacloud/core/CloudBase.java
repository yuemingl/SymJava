package lambdacloud.core;

import symjava.symbolic.Expr;

public class CloudBase extends Expr {

	@Override
	public Expr simplify() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean symEquals(Expr other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Expr diff(Expr expr) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void apply(CloudVar ...inputs) {
		
	}
}
