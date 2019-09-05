package io.lambdacloud.symjava.examples;

import java.util.ArrayList;
import java.util.List;

import io.lambdacloud.symjava.relational.Eq;
import io.lambdacloud.symjava.symbolic.Expr;
import io.lambdacloud.symjava.symbolic.Sum;
import io.lambdacloud.symjava.symbolic.Symbol;
import io.lambdacloud.symjava.symbolic.Symbols;
import io.lambdacloud.symjava.symbolic.utils.Utils;
import static io.lambdacloud.symjava.symbolic.Symbol.*;
import static io.lambdacloud.symjava.math.SymMath.*;


public class LagrangeMultipliers {
	Eq eq;
	double[] init;
	double[][] data;
	
	
	public LagrangeMultipliers(Eq eq, double [] init, double[][] data) {
		this.eq = eq;
		this.init = init;
		this.data = data;
	}
	
	public Eq getEqForDisplay() {
		Symbol idx = new Symbol("i");
		Symbols lambdas = new Symbols("\\lambda", idx);
		List<Expr> addList = new ArrayList<Expr>();
		Expr[] freeVars = eq.getFreeVars();
		Expr[] depVars = eq.getDependentVars();
		
		Expr[] freeVarForL = new Expr[data.length*depVars.length + data.length + eq.getParams().length];
		int lmdIdxStart = data.length*depVars.length;
		for(int i=0; i<data.length; i++) {
			for(int j=0; j<depVars.length; j++) {
				int yIdx = (j*data.length)+i;
				Symbols ys = new Symbols(depVars[j].toString(), idx);
				freeVarForL[yIdx] = ys.get(yIdx);
			}
			freeVarForL[lmdIdxStart + i] = lambdas.get(i);
		}
		
		List<Expr> targets = new ArrayList<Expr>();
		for(int j=0; j<depVars.length; j++) {
			Expr depDataSymbols = new Symbols(depVars[j].toString().toUpperCase(), idx);
			Expr depSymbols = new Symbols(depVars[j].toString(), idx);
			targets.add(new Sum(pow(depDataSymbols - depSymbols, 2), idx, 0, data.length-1));
		}
		addList.addAll(targets);
		Expr state_eq = eq.arg1;
		for(int j=0; j<freeVars.length; j++) {
			state_eq = state_eq.subs(freeVars[j], new Symbols(freeVars[j].toString().toUpperCase(), idx));
		}
		for(int j=0; j<depVars.length; j++) {
			state_eq = state_eq.subs(depVars[j], new Symbols(depVars[j].toString(), idx));
		}
		Sum sum = new Sum(lambdas*state_eq, idx, 0, data.length-1);
		addList.add(sum);
		for(int k=0; k<eq.getParams().length; k++)
			freeVarForL[data.length*depVars.length + data.length + k] = eq.getParams()[k];
		Expr addExpr = Utils.addListToExpr(addList);
		Expr ret = Utils.flattenSortAndSimplify(addExpr);
		return new Eq(ret, C0, freeVarForL);
	}
	
	public Eq getEq() {
		Symbol idx = new Symbol("i");
		Symbols lambdas = new Symbols("\\lambda");
		List<Expr> addList = new ArrayList<Expr>();
		Expr[] freeVars = eq.getFreeVars();
		Expr[] depVars = eq.getDependentVars();
		
		int depVarIdxStart = freeVars.length;
		Expr[] freeVarForL = new Expr[data.length*depVars.length + data.length + eq.getParams().length];
		int lmdIdxStart = data.length*depVars.length;
		for(int i=0; i<data.length; i++) {
			Expr state_eq = eq.arg1;
			for(int j=0; j<depVars.length; j++) {
				int yIdx = (j*data.length)+i;
				Symbols ys = new Symbols(depVars[j].toString(), idx);
				Expr yi = ys.get(yIdx);
				freeVarForL[yIdx] = yi;
				//addList.add(new Power(-ys.get(yIdx) + data[i][depVarIdxStart+j], 2)/2);
				addList.add(pow(-ys.get(yIdx) + data[i][depVarIdxStart+j], 2));
				state_eq = state_eq.subs(depVars[j], ys.get(yIdx));
			}
			Expr lmdi = lambdas.get(i);
			freeVarForL[lmdIdxStart + i] = lmdi;
			for(int j=0; j<freeVars.length; j++)
				state_eq = state_eq.subs(freeVars[j], data[i][j]);
			addList.add(lmdi*state_eq);
		}
		for(int i=0; i<eq.getParams().length; i++)
			freeVarForL[data.length*depVars.length + data.length + i] = eq.getParams()[i];
		Expr ret = Utils.flattenSortAndSimplify(Utils.addListToExpr(addList));
		return new Eq(ret, C0, freeVarForL);
	}
	
	public Expr[] getUnknows() {
		return null;
	}
	
	public double[] getInitialGuess() {
		Expr[] depVars = eq.getDependentVars();
		double[] ret = new double[data.length*depVars.length + data.length + eq.getParams().length];
		for(int i=0; i<data.length*depVars.length + data.length; i++)
			ret[i] = 0;//Math.random();
		for(int i=0; i<eq.getParams().length; i++)
			ret[data.length*depVars.length + data.length + i] = init[i];		
		return ret;
	}
}
