package symjava.symbolic;

import java.util.List;

public interface Domain {
	Domain getBoundary();
	List<Expr> getCoordVars();
}
