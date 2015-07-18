package symjava.relational;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.DCMPL;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFLE;
import com.sun.org.apache.bcel.internal.generic.IFNE;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.NOP;
import com.sun.org.apache.bcel.internal.generic.PUSH;

import symjava.symbolic.Expr;
import symjava.symbolic.Expr.TYPE;
import symjava.symbolic.Symbol;
import symjava.symbolic.arity.BinaryOp;
import symjava.symbolic.utils.Utils;

/**
 * An object of Eq represents an equation like
 * y == a*x+b
 * 
 */
public class Eq extends BinaryOp implements Relation {
	Expr[] freeVars; //for example: x in y=a*x+b
	Expr[] dependentVars; //for example: y in y=a*x+b
	Expr[] params; //paramters in the equation, for example: a, b in y=a*x+b
	Expr[] unknowns; //freeVars + dependentVars, for example: x, y in y=a*x+b
	
	/**
	 * Create an equation without any symbolic parameters. 
	 * The free variables and dependent variables are extracted 
	 * from the lhs and rhs expressions automatically
	 * 
	 * Examples:
	 * Eq eq1 = new Eq(y, 2*x+1);    // y = 2*x+1
	 *   free variables: [x]
	 *   dependent variables: [y]
	 *   unknowns: [x,y]
	 * Eq eq2 = new Eq(x^2-2*x+1,0); // x^2-2*x+1 = 0
	 *   free variables: [x]
	 *   dependent variables:
	 *   unknowns: [x]
	 * Eq eq3 = new Eq(cos(x), x); // cos(x) = x
	 *   free variables: [x]
	 *   dependent variables: [x]
	 *   unknowns: [x]
	 * Note: In definition of eq3, one may need to pass more parameters to exactly 
	 *    define free variables and dependent variables 
	 *   
	 * @param lhs
	 * @param rhs
	 */
	public Eq(Expr lhs, Expr rhs) {
		super(lhs, rhs);
		this.label = arg1 + " == " + arg2;
		this.sortKey = this.label;
		this.params = new Expr[0];
		Expr[] rhsVars = Utils.extractSymbols(rhs).toArray(new Expr[0]);
		Expr[] lhsVars = Utils.extractSymbols(lhs).toArray(new Expr[0]);
		if(rhsVars.length == 0) {
			this.freeVars = lhsVars;
			this.dependentVars = new Expr[0];
		} else {
			this.freeVars = rhsVars;
			this.dependentVars = lhsVars;
		}
		this.unknowns = Utils.extractSymbols(lhs, rhs).toArray(new Expr[0]);
	}
	
	/**
	 * Create an equation that may contain symbolic parameters on the right hand side.
	 * The free variables are specified by the parameter freeVars
	 * The parameters on the right hand side of the equation are extracted automatically.
	 * 
	 * Examples:
	 * Eq eq = Eq(y, a*x+b, new Expr[]{x}); 
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
		this.label = arg1 + " == " + arg2;
		this.sortKey = this.label;
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
		computeUnknowns();
	}

	/**
	 * Create an equation by specifying free variables and symbolic parameters
	 * 
	 * @param lhs
	 * @param rhs
	 * @param freeVars
	 * @param params
	 */
	public Eq(Expr lhs, Expr rhs, Expr[] freeVars, Expr[] params) {
		super(lhs, rhs);
		this.label = arg1 + " == " + arg2;
		this.sortKey = this.label;
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
		computeUnknowns();
	}
	
	/**
	 * Create an equation by specifying free variables, symbolic parameters and dependent variables
	 * 
	 * @param lhs
	 * @param rhs
	 * @param freeVars
	 * @param params
	 * @param dependentVars
	 */
	public Eq(Expr lhs, Expr rhs, Expr[] freeVars, Expr[] params, Expr[] dependentVars) {
		super(lhs, rhs);
		this.label = arg1 + " == " + arg2;
		this.sortKey = this.label;
		this.freeVars = freeVars;
		this.params = params;
		this.dependentVars = dependentVars;
		computeUnknowns();
	}
	
	public String toString() {
		String sFreeVars = "";
		if(this.freeVars.length > 0) {
			sFreeVars = ", array("+Utils.joinLabels(this.freeVars, ",")+")";
		}
		
		String sParams = "";
		if(this.params.length > 0) {
			sParams += ", array("+Utils.joinLabels(this.params, ",")+")";
		}
		
		return "eq("+arg1+", "+arg2+sFreeVars+sParams+")";
	}
	
	private void computeUnknowns() {
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
	public static Eq apply(double lhs, Expr rhs) {
		return new Eq(Expr.valueOf(lhs), rhs);
	}
	public static Eq apply(int lhs, Expr rhs) {
		return new Eq(Expr.valueOf(lhs), rhs);
	}
	public static Eq apply(Expr lhs, double rhs) {
		return new Eq(lhs, Expr.valueOf(rhs));
	}
	public static Eq apply(Expr lhs, int rhs) {
		return new Eq(lhs, Expr.valueOf(rhs));
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
	
	/**
	 * Return an array of variables that contains free variables and dependent variables
	 * @return
	 */
	public Expr[] getUnknowns() {
		return unknowns;
	}
	
	/**
	 * Return an array of symbols parameters in the expression of the equation
	 * @return
	 */
	public Expr[] getParams() {
		return params;
	}
	
	/**
	 * Return an array of free variables in the equation
	 * @return
	 */
	public Expr[] getFreeVars () {
		return freeVars;
	}
	
	/**
	 * Return an array of dependent variables in the equation
	 * @return
	 */
	public Expr[] getDependentVars() {
		return dependentVars;
	}
	
	/**
	 * TODO
	 * @param var
	 * @return
	 */
	public Expr solve(Expr var) {
		return null;
	}

	@Override
	public Expr simplify() {
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		return false;
	}

	/**
	 * Differentiate both side of the equation
	 */
	@Override
	public Expr diff(Expr expr) {
		return new Eq(arg1.diff(expr), arg2.diff(expr), this.freeVars, this.params, this.dependentVars);
	}

	
	public InstructionHandle bytecodeGen(String clsName, MethodGen mg,
			ConstantPoolGen cp, InstructionFactory factory,
			InstructionList il, Map<String, Integer> argsMap, int argsStartPos, 
			Map<Expr, Integer> funcRefsMap) {
		InstructionHandle startPos = arg1.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		arg2.bytecodeGen(clsName, mg, cp, factory, il, argsMap, argsStartPos, funcRefsMap);
		il.append(new DCMPL());
		InstructionHandle iconst1 = il.append(new PUSH(cp, 1));
		InstructionHandle iconst0 = il.append(new PUSH(cp, 0));
		InstructionHandle nop = il.append(new NOP());
		il.insert(iconst1, new IFNE(iconst0));
		il.insert(iconst0, new GOTO(nop));
		return startPos;
	}
	
	@Override
	public TYPE getType() {
		return TYPE.INT;
	}
	
	public void moveRHS2LHS() {
		this.arg1 = this.arg1 - this.arg2;
		this.arg2 = Symbol.C0;
	}
}

