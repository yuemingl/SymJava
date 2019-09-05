package io.lambdacloud.symjava.domains;

import io.lambdacloud.symjava.math.Transformation;
import io.lambdacloud.symjava.symbolic.Expr;
import io.lambdacloud.symjava.symbolic.SymDouble;
import static io.lambdacloud.symjava.symbolic.Symbol.x;

public class Interval extends Domain1D {
	Expr start;
	Expr end;
	
	public Interval(Expr start, Expr end) {
		super("["+start+","+end+"]", x);
		this.start = start;
		this.end = end;
	}
	
	public Interval(Expr start, Expr end, Expr coordVar) {
		super("["+start+","+end+"]", coordVar);
		this.start = start;
		this.end = end;
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
	public static <T1, T2> Interval apply(T1 start, T2 end) {
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
		return new Interval(s, e);
	}
	
	public static <T1, T2> Interval apply(T1 start, T2 end, Expr coordVar) {
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
		return new Interval(s, e, coordVar);
	}	
	
	@Override
	public String toString() {
		return "interval("+this.getCoordVars()[0]+","+start+","+end+","+this.getStepSize()+")";
	}
	
	@Override
	public Domain transform(String label, Transformation trans) {
		Expr from = trans.getFromVars()[0];
		Expr to = trans.getToVars()[0];
		Expr toSolve = trans.eqs[0].solve(to);
		return new Interval(
				toSolve.subs(from, start),
				toSolve.subs(from, end),
				to);
	}

}
