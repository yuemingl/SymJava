package symjava.math;

import java.util.HashSet;

import symjava.matrix.ExprMatrix;
import symjava.relational.Eq;
import symjava.symbolic.Expr;
import symjava.symbolic.Func;
import symjava.symbolic.Symbol;
import symjava.symbolic.utils.Utils;

/**
 * Transformation is a system of equations that define the change of variables
 * For example:
 * x = r + s
 * y = r - s
 * This will change (x,y) to (r+s, r-s)
 *
 */
public class Transformation {
	public Eq[] eqs = null;
	
	public Transformation(Eq ...eqs) {
		for(Eq e : eqs) {
			if(!(e.arg1 instanceof Symbol || e.arg1 instanceof Func))
				throw new RuntimeException("The left hand side must be a symbol or function!");
		}
		this.eqs = eqs;
	}
	
	public Expr[] getFromVars() {
		Expr[] rlt = new Expr[eqs.length];
		for(int i=0; i<rlt.length; i++) {
			rlt[i] = eqs[i].arg1;
		}
		return rlt;
	}
	
	public Expr[] getToVars() {
		Expr[] rlt = new Expr[eqs.length];
		HashSet<Expr> set = new HashSet<Expr>();
		for(Eq e : eqs) {
			Expr[] freeVars = e.getFreeVars();
			for(Expr expr : freeVars)
				set.add(expr);
		}
		rlt = set.toArray(new Expr[0]);
		Utils.sortExprs(rlt);
		return rlt;		
	}
	
	public Expr getJacobian() {
		
		return getJacobianMatrix().det();
	}
	
	public ExprMatrix getJacobianMatrix() {
		Expr[] fromVars = this.getFromVars();
		Expr[] toVars = this.getToVars();
		ExprMatrix m = new ExprMatrix(fromVars.length, toVars.length);
		for(int i=0; i<fromVars.length; i++) {
			for(int j=0; j<toVars.length; j++) {
				m.set(i, j, eqs[i].arg2.diff(toVars[j]));
			}
		}
		return m;
	}
	
}
