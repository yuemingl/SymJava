package symjava.symbolic;

import symjava.symbolic.utils.Utils;

/**
 * Integration class
 * 
 */
public class Int extends Expr {
	Expr integrand = null;
	Domain domain = null;
	
	public Int(Expr integrand, Domain domain) {
		this.integrand = integrand;
		this.domain = domain;
		String postfix = "d" + Utils.joinLabels(domain.getCoordVars(),"d");
		if(domain instanceof Domain1D) {
			Domain1D o = (Domain1D)domain;
			this.label = "\\int_{"+o.getStart()+"}^{"+o.getEnd()+"}{"+integrand+"}" + postfix;
		}
		else
			this.label = "\\int_{"+domain+"}{"+integrand+"}" + postfix;
		this.sortKey = integrand.toString()+domain.toString();
	}
	
	public static Expr apply(Expr integrand, Domain domain) {
		return new Int(integrand, domain);
	}
	
	@Override
	public Expr diff(Expr expr) {
		return null;
	}

	@Override
	public Expr simplify() {
		return null;
	}

	@Override
	public boolean symEquals(Expr other) {
		return false;
	}

}
