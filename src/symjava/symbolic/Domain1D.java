package symjava.symbolic;

import symjava.math.Transformation;

public class Domain1D extends Domain {
	Expr start;
	Expr end;
	public Domain1D(Expr start, Expr end) {
		this.start = start;
		this.end = end;
		this.coordVars = new Expr[] { Symbol.x };
	}
	public Domain1D(Expr start, Expr end, Expr coordVar) {
		this.start = start;
		this.end = end;
		this.coordVars = new Expr[] { coordVar };
		this.label = "["+start+","+end+"]";
	}	
	
	public Expr getStart() {
		return start;
	}
	
	public Expr getEnd() {
		return end;
	}
	
	/**
	 * Define a interval by giving two number of the bounds
	 * @param start
	 * @param end
	 * @return
	 */
	public static <T1, T2> Domain apply(T1 start, T2 end) {
		Expr s = null, e = null;
		if(start instanceof Number) {
			s = new SymDouble(((Number)start).doubleValue());
		} else {
			s = (Expr)start;
		}
		if(end instanceof Number) {
			e = new SymDouble(((Number)end).doubleValue());
		} else {
			e = (Expr)end;
		}
		return new Domain1D(s, e);
	}
	
	@Override
	public String toString() {
		return "["+start+","+end+"]";
	}
	
	@Override
	public Domain transform(String label, Transformation trans) {
		Expr from = trans.getFromVars()[0];
		Expr to = trans.getToVars()[0];
		Expr toSolve = trans.eqs[0].solve(to);
		return new Domain1D(
				toSolve.subs(from, start),
				toSolve.subs(from, end),
				to);
	}
	
	@Override
	public int getDim() {
		return 1;
	}
}
