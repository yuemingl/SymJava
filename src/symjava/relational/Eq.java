package symjava.relational;

import java.util.ArrayList;
import java.util.List;

import symjava.symbolic.Expr;
import symjava.symbolic.arity.BinaryOp;
import symjava.symbolic.utils.Utils;

/**
 * An object of Eq represents a equation like
 * y = a*x+b
 * 
 */
public class Eq extends BinaryOp implements Relation {
	Expr[] freeVars; //for example: x in y=a*x+b
	Expr[] dependentVars; //for example: y in y=a*x+b
	Expr[] params; //paramters in the equation, for example: a, b in y=a*x+b
	Expr[] unknowns; //freeVars + dependentVars, for example: x, y in y=a*x+b
	
	/**
	 * Create an equation like y=... without any parameters
	 * For example:
	 * Eq e = new Eq(y, 2*x+1); //y = 2*x+1
	 * 
	 * @param lhs
	 * @param rhs
	 */
	public Eq(Expr lhs, Expr rhs) {
		super(lhs, rhs);
		this.freeVars = Utils.extractSymbols(rhs).toArray(new Expr[0]);
		this.params = new Expr[0];
		this.dependentVars = Utils.extractSymbols(lhs).toArray(new Expr[0]);
		setUnknowns();
	}
	
	/**
	 * Create an equation like y=... 
	 * The free variables are specified on the right hand side (rhs).
	 * It may contain parameters on the right hand side and the paramters
	 * can be extracted automatically.
	 * For example:
	 * Eq e = Eq(y, a*x+b, new Expr[]{x}); 
	 * This will create equation y=a*x+b with
	 *   freeVars = [x]
	 *   params = [a,b]
	 *   dependentVars = [y]
	 *   unkonws = [x,y]
	 * 
	 * @param lhs
	 * @param rhs
	 * @param freeVars
	 */
	public Eq(Expr lhs, Expr rhs, Expr[] freeVars) {
		super(lhs, rhs);
		this.freeVars = freeVars;
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
		
		//Find params
		list = Utils.extractSymbols(rhs);
		List<Expr> paramList = new ArrayList<Expr>();
		for(Expr s : list) {
			boolean skip = false;
			for(int i=0; i<freeVars.length; i++) {
				if(s.symEquals(freeVars[i])) {
					skip = true;
					break;
				}
			}
			if(skip) continue;
			paramList.add(s);
		}
		this.params = paramList.toArray(new Expr[0]);
		setUnknowns();
	}

	public Eq(Expr lhs, Expr rhs, Expr[] freeVars, Expr[] params) {
		super(lhs, rhs);
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
		setUnknowns();
	}
	
	/**
	 * 
	 * @param lhs
	 * @param rhs
	 * @param freeVars
	 * @param params
	 * @param dependentVars
	 */
	public Eq(Expr lhs, Expr rhs, Expr[] freeVars, Expr[] params, Expr[] dependentVars) {
		super(lhs, rhs);
		this.freeVars = freeVars;
		this.params = params;
		this.dependentVars = dependentVars;
		setUnknowns();
	}
	
	private void setUnknowns() {
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
		Expr rlt = arg1;
		for(int i=0; i<from.length; i++)
			rlt = rlt.subs(from[i], to[i]);
		return rlt;
	}
	
	public Expr subsRHS(Expr[] from, Expr[] to) {
		Expr rlt = arg2;
		for(int i=0; i<from.length; i++)
			rlt = rlt.subs(from[i], to[i]);
		return rlt;
	}
	
	public Expr subsLHS(Expr[] from, double[] to) {
		Expr rlt = arg1;
		for(int i=0; i<from.length; i++)
			rlt = rlt.subs(from[i], to[i]);
		return rlt;
	}
	
	public Expr subsRHS(Expr[] from, double[] to) {
		Expr rlt = arg2;
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
	
	public String toString() {
		return arg1 + " = " + arg2;
	}

	@Override
	public Expr simplify() {
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		return false;
	}

	@Override
	public Expr diff(Expr expr) {
		return new Eq(arg1.diff(expr), arg2.diff(expr), this.freeVars, this.params, this.dependentVars);
	}
	
	public Expr lhs() {
		return arg1;
	}
	
	public Expr rhs() {
		return arg2;
	}
}

