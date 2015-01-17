package symjava.relational;

import symjava.symbolic.Expr;

public class Eq extends Relation {
	Expr[] unknowns;
	Expr[] params;
	
	public Eq(Expr lhs, Expr rhs, Expr[] unknowns, Expr[] params) {
		this.lhs = lhs;
		this.rhs = rhs;
		this.unknowns = unknowns;
		this.params = params;
	}
	
	public Expr subsLHS(Expr[] from, Expr[] to) {
		Expr rlt = lhs;
		for(int i=0; i<from.length; i++)
			rlt = rlt.subs(from[i], to[i]);
		return rlt;
	}
	
	public Expr subsRHS(Expr[] from, Expr[] to) {
		Expr rlt = rhs;
		for(int i=0; i<from.length; i++)
			rlt = rlt.subs(from[i], to[i]);
		return rlt;
	}
	
	public Expr subsLHS(Expr[] from, double[] to) {
		Expr rlt = lhs;
		for(int i=0; i<from.length; i++)
			rlt = rlt.subs(from[i], to[i]);
		return rlt;
	}
	
	public Expr subsRHS(Expr[] from, double[] to) {
		Expr rlt = rhs;
		for(int i=0; i<from.length; i++)
			rlt = rlt.subs(from[i], to[i]);
		return rlt;
	}
	
	public Eq subsUnknowns(double[] data) {
		return new Eq(
				this.subsLHS(unknowns, data),
				this.subsRHS(unknowns, data),
				this.unknowns,
				this.params
				);
	}
	
	public Eq subsParams(double[] data) {
		return new Eq(
				this.subsLHS(params, data),
				this.subsRHS(params, data),
				this.unknowns,
				this.params
				);
	}
	
	public Expr[] getUnknowns() {
		return unknowns;
	}
	
	public Expr[] getParams() {
		return params;
	}
}
