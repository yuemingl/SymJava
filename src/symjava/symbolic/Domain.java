package symjava.symbolic;

import symjava.math.Transformation;

public interface Domain {
	Domain getBoundary();
	Expr[] getCoordVars();
	Domain transform(String label, Transformation trans);
	String getLabel();
}
