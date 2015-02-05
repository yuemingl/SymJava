package symjava.symbolic;

import java.util.ArrayList;
import java.util.List;

public class Domain1D implements Domain {
	Expr coordVar = Symbol.x;
	Expr start;
	Expr end;
	public Domain1D(Expr start, Expr end) {
		this.start = start;
		this.end = end;
	}
	public Domain1D(Expr start, Expr end, Expr coordVar) {
		this.start = start;
		this.end = end;
		this.coordVar = coordVar;
	}	
	
	public Expr getStart() {
		return start;
	}
	
	public Expr getEnd() {
		return end;
	}
	
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
	public Domain getBoundary() {
		return null;
	}

	@Override
	public List<Expr> getCoordVars() {
		List<Expr> l = new ArrayList<Expr>();
		l.add(this.coordVar);
		return l;
	}
	
	@Override
	public String toString() {
		return "["+start+","+end+"]";
	}
}
