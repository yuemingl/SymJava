package symjava.domains;

import symjava.math.Transformation;
import symjava.symbolic.Expr;
import symjava.symbolic.Symbol;

public class Domain3D extends Domain {
	/**
	 * Construct a 3D domain with a given label(name) and
	 * a list of coordinate variables
	 * @param label
	 * @param coordVars
	 */
	public Domain3D(String label, Expr ...coordVars) {
		this.label = label;
		this.coordVars = coordVars;
	}
	
	/**
	 * Construct a 3D domain with a given label(name) and with
	 * default coordinate variables x,y and z
	 * 
	 * @param label
	 */
	public Domain3D(String label) {
		this.label = label;
		this.coordVars = new Expr[]{Symbol.x, Symbol.y, Symbol.z};
	}
	
	@Override
	public Domain transform(String label, Transformation trans) {
		return new Domain3D(label, trans.getToVars());
	}

	@Override
	public int getDim() {
		return 3;
	}
}
