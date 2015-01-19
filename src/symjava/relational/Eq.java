package symjava.relational;

import java.util.ArrayList;
import java.util.List;

import symjava.symbolic.Expr;
import symjava.symbolic.Symbol;
import symjava.symbolic.utils.Utils;

public class Eq extends Relation {
	Expr[] freeVars;
	Expr[] dependentVars;
	Expr[] params;
	Expr[] unknowns;
	
	public Eq(Expr lhs, Expr rhs, Expr[] freeVars, Expr[] params) {
		this.lhs = lhs;
		this.rhs = rhs;
		this.freeVars = freeVars;
		this.params = params;
		//Find dependent variables
		List<Symbol> list = Utils.extractSymbols(lhs, rhs);
		List<Expr> depList = new ArrayList<Expr>();
		for(Symbol s : list) {
			boolean skip = false;
			for(int i=0; i<freeVars.length; i++) {
				if(s.symEquals(freeVars[i])) skip = true;
			}
			if(skip) continue;
			for(int i=0; i<params.length; i++) {
				if(s.symEquals(params[i])) skip = true;
			}
			if(skip) continue;
			depList.add(s);
		}
		dependentVars = depList.toArray(new Expr[0]);
		this.unknowns = new Expr[freeVars.length + dependentVars.length];
		int idx = 0;
		for(int i=0; i<freeVars.length; i++) {
			unknowns[idx++] = freeVars[i];
		}
		for(int i=0; i<dependentVars.length; i++) {
			unknowns[idx++] = dependentVars[i];
		}
	}
	
	public Eq(Expr lhs, Expr rhs, Expr[] freeVars, Expr[] params, Expr[] dependentVars) {
		this.lhs = lhs;
		this.rhs = rhs;
		this.freeVars = freeVars;
		this.dependentVars = dependentVars;
		this.params = params;
		this.unknowns = new Expr[freeVars.length + dependentVars.length];
		int idx = 0;
		for(int i=0; i<freeVars.length; i++) {
			unknowns[idx++] = freeVars[i];
		}
		for(int i=0; i<dependentVars.length; i++) {
			unknowns[idx++] = dependentVars[i];
		}
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
				this.freeVars,
				this.params,
				this.dependentVars
				);
	}
	
	public Eq subsParams(double[] data) {
		return new Eq(
				this.subsLHS(params, data),
				this.subsRHS(params, data),
				this.freeVars,
				this.params,
				this.dependentVars
				);
	}
	
	public Expr[] getUnknowns() {
		return unknowns;
	}
	
	public Expr[] getParams() {
		return params;
	}
	
	public Expr[] getFreeVars () {
		return freeVars;
	}
	
	public Expr[] getDependentVars() {
		return dependentVars;
	}
}
