package symjava.examples;

import java.util.ArrayList;
import java.util.List;

import symjava.relational.Eq;
import symjava.symbolic.Expr;
import symjava.symbolic.Power;
import symjava.symbolic.Symbols;
import symjava.symbolic.utils.Utils;
import static symjava.symbolic.Symbol.*;

public class LagrangeMultipliers {
	Eq eq;
	double[] init;
	double[][] data;
	
	
	public LagrangeMultipliers(Eq eq, double [] init, double[][] data) {
		this.eq = eq;
		this.init = init;
		this.data = data;
	}
	
	public Eq getEq() {
		Symbols ys = new Symbols("y");
		Symbols lambdas = new Symbols("\\lambda");
		List<Expr> addList = new ArrayList<Expr>();
		Expr[] freeVars = eq.getFreeVars();
		Expr[] depVars = eq.getDependentVars();
		
		int depVarIdxStart = freeVars.length;
		Expr[] freeVarForL = new Expr[data.length*depVars.length + data.length + eq.getParams().length];
		int lmdIdxStart = data.length*depVars.length;
		for(int i=0; i<data.length; i++) {
			Expr state_eq = eq.lhs;
			for(int j=0; j<depVars.length; j++) {
				int yIdx = (j*data.length)+i;
				Expr yi = ys.get(yIdx);
				freeVarForL[yIdx] = yi;
				//addList.add(new Power(-ys.get(yIdx) + data[i][depVarIdxStart+j], 2)/2);
				addList.add(Power.simplifiedIns(-ys.get(yIdx) + data[i][depVarIdxStart+j], 2));
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
		return new Eq(ret, C0, freeVarForL , null);
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
