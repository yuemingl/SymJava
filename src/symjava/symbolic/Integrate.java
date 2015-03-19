package symjava.symbolic;

import java.util.List;

import symjava.domains.Domain;
import symjava.domains.Interval;
import symjava.math.Transformation;
import symjava.relational.Eq;
import symjava.symbolic.utils.ExprPair;
import symjava.symbolic.utils.Utils;

/**
 * Class represents an integration
 * 
 */
public class Integrate extends Expr {
	public Expr integrand = null;
	public Domain domain = null;
	
	public Integrate(Expr integrand, Domain domain) {
		this.integrand = integrand;
		this.domain = domain;
		String postfix = "d" + Utils.joinLabels(domain.getCoordVars(),"d");
		if(domain instanceof Interval) {
			Interval o = (Interval)domain;
			this.label = "\\int_{"+o.getStart()+"}^{"+o.getEnd()+"}{"+integrand+"}" + postfix;
		}
		else
			this.label = "\\int_{"+domain+"}{"+integrand+"}" + postfix;
		this.sortKey = integrand.toString()+domain.toString();
	}
	
	public static Expr apply(Expr integrand, Domain domain) {
		return new Integrate(integrand, domain);
	}
	
	@Override
	public Expr subs(Expr from, Expr to) {
		return new Integrate(this.integrand.subs(from, to), this.domain);
	}
	
	/**
	 * Change of Variables
	 * @param trans
	 * @return
	 */
	public Integrate changeOfVars(Transformation trans) {
		Expr tmp = this.integrand;
		for(Eq e : trans.eqs) {
			tmp = tmp.subs(e.lhs, e.rhs);
		}
		
		//For test
		//return new Int(tmp.multiply(new Func("Jac")), 
		//		this.domain.transform(this.domain.getLabel()+"T", trans));
		
		Expr jac = trans.getJacobian();
		return new Integrate(new Func(this.integrand.getLabel(), tmp.multiply(jac), trans.getToVars()), 
				this.domain.transform(this.domain.getLabel()+"T", trans));
	}
	
	public Integrate changeOfVars(List<ExprPair> subsList, Expr jac, Domain target) {
		Expr tmp = this.integrand;
		for(ExprPair p : subsList) {
			tmp = tmp.subs(p.e1, p.e2);
		}
		return new Integrate(new Func(
				this.label, tmp.multiply(jac), 
				target.getCoordVars()
				), target);
	}
	
	@Override
	public Expr diff(Expr expr) {
		if(domain instanceof Interval) {
			Interval I = (Interval)domain;
			Expr end = I.getEnd();
			Expr intSubs = integrand.subs(domain.getCoordVars()[0], end);
			return intSubs.multiply(end.diff(expr));
		}
		return null;
	}

	@Override
	public Expr simplify() {
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		return false;
	}

}
