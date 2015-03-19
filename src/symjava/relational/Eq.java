package symjava.relational;

import java.util.ArrayList;
import java.util.List;

import symjava.symbolic.Expr;
import symjava.symbolic.utils.Utils;

public class Eq extends Relation {
	Expr[] freeVars; //for example: x in y=a*x+b
	Expr[] dependentVars; //for example: y in y=a*x+b
	Expr[] params; //paramters in the equation, for example: a, b in y=a*x+b
	Expr[] unknowns; //freeVars + dependentVars, for example: x, y in y=a*x+b
	
	public Eq(Expr lhs, Expr rhs) {
		this.lhs = lhs;
		this.rhs = rhs;	
		this.freeVars = new Expr[0];;
		this.params = new Expr[0];
	}
	
	public Eq(Expr lhs, Expr rhs, Expr[] freeVars) {
		this.lhs = lhs;
		this.rhs = rhs;	
		this.freeVars = freeVars;
		this.params = new Expr[0];
		//Find dependent variables
		List<Expr> list = Utils.extractSymbols(lhs, rhs);
		List<Expr> depList = new ArrayList<Expr>();
		for(Expr s : list) {
			boolean skip = false;
			for(int i=0; i<freeVars.length; i++) {
				if(s.symEquals(freeVars[i])) skip = true;
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

	public Eq(Expr lhs, Expr rhs, Expr[] freeVars, Expr[] params) {
		this.lhs = lhs;
		this.rhs = rhs;
		this.freeVars = freeVars;
		this.params = params;
		//Find dependent variables
		List<Expr> list = Utils.extractSymbols(lhs, rhs);
		List<Expr> depList = new ArrayList<Expr>();
		for(Expr s : list) {
			boolean skip = false;
			for(int i=0; i<freeVars.length; i++) {
				if(s.symEquals(freeVars[i])) skip = true;
			}
			if(skip) continue;
			if(params == null) continue;
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

	public static Eq apply(Expr lhs, Expr rhs) {
		return new Eq(lhs, rhs);
	}

	public static Eq apply(Expr lhs, Expr rhs, Expr[] freeVars) {
		return new Eq(lhs, rhs, freeVars);
	}
	
	public static Eq apply(Expr lhs, Expr rhs, Expr[] freeVars, Expr[] params) {
		return new Eq(lhs, rhs, freeVars, params);
	}
	
	public static Eq apply(Expr lhs, Expr rhs, Expr[] freeVars, Expr[] params, Expr[] dependentVars) {
		return new Eq(lhs, rhs, freeVars, params, dependentVars);
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
	
	public Expr solve(Expr var) {
		return null;
	}
}
