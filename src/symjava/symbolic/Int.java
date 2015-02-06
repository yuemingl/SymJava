package symjava.symbolic;

import symjava.math.Transformation;
import symjava.relational.Eq;
import symjava.symbolic.utils.Utils;

/**
 * Integration class
 * 
 */
public class Int extends Expr {
	public Expr integrand = null;
	public Domain domain = null;
	
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
	public Expr subs(Expr from, Expr to) {
		return new Int(this.integrand.subs(from, to), this.domain);
	}
	
	/**
	 * Change of Variables
	 * @param trans
	 * @return
	 */
	public Int changeOfVars(Transformation trans) {
		Expr tmp = this.integrand;
		for(Eq e : trans.eqs) {
			tmp = tmp.subs(e.lhs, e.rhs);
		}
		
		//return new Int(tmp.multiply(new Func("Jac")), 
		//		this.domain.transform(this.domain.getLabel()+"T", trans));
		Expr jac = trans.getJacobian();
		return new Int(tmp.multiply(jac), 
				this.domain.transform(this.domain.getLabel()+"T", trans));
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
