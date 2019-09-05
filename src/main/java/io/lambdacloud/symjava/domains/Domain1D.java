package io.lambdacloud.symjava.domains;

import io.lambdacloud.symjava.math.Transformation;
import io.lambdacloud.symjava.symbolic.Expr;

public class Domain1D extends Domain {
	
	public Domain1D(String label, Expr coordVar) {
		this.label = label;
		this.coordVars = new Expr[] { coordVar };
	}

	@Override
	public Domain transform(String label, Transformation trans) {
		return new Domain1D(label, trans.getToVars()[0]);
	}

	@Override
	public int getDim() {
		return 1;
	}
}
