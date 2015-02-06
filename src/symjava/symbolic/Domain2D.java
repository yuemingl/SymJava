package symjava.symbolic;

import java.util.List;

import symjava.math.Transformation;

public class Domain2D implements Domain {
	String label = null;
	Expr[] coordVars = null;

	/**
	 * Construct a 2D domain with a given label(name) and
	 * a list of coordinate variables
	 * @param label
	 * @param coordVars
	 */
	public Domain2D(String label, Expr ...coordVars) {
		this.label = label;
		this.coordVars = coordVars;
	}	
	
	public Domain2D(String label, List<Expr> coordVars) {
		this.label = label;
		this.coordVars = coordVars.toArray(new Expr[0]);
	}
	
	@Override
	public Domain getBoundary() {
		return null;
	}

	@Override
	public Expr[] getCoordVars() {
		return this.coordVars;
	}

	@Override
	public String toString() {
		return this.label;
	}

	@Override
	public Domain transform(String label, Transformation trans) {
		return new Domain2D(label, trans.getToVars());
	}

	@Override
	public String getLabel() {
		return label;
	}

}
