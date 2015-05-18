package symjava.domains;

import symjava.math.Transformation;
import symjava.symbolic.Expr;

public class DomainND extends Domain {
	/**
	 * Construct a N Dimentional domain with a given label(name) and
	 * a list of coordinate variables
	 * @param label
	 * @param coordVars
	 */
	public DomainND(String label, Expr ...coordVars) {
		this.label = label;
		this.coordVars = coordVars;
	}
	
	@Override
	public Domain transform(String label, Transformation trans) {
		return new DomainND(label, trans.getToVars());
	}

	@Override
	public int getDim() {
		return coordVars.length;
	}
}
