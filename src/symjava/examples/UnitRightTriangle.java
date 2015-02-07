package symjava.examples;

import java.util.List;

import symjava.symbolic.Domain2D;
import symjava.symbolic.Expr;
import static symjava.symbolic.Symbol.*;


public class UnitRightTriangle extends Domain2D {

	public UnitRightTriangle(String label, Expr ...coordVars) {
		super(label, coordVars);
	}
	
	public UnitRightTriangle(String label, List<Expr> coordVars) {
		super(label, coordVars);
	}
	
	@Override
	public double[][] getIntWeightAndPoints(int order) {
		double[][] rlt = {
				{0.10128651, 0.06296959, 0.10128651},
				{0.47014206, 0.06619708, 0.05971587},
				{0.79742699, 0.06296959, 0.10128651},
				{0.47014206, 0.06619708, 0.47014206},
				{0.10128651, 0.06296959, 0.79742699},
				{0.05971587, 0.06619708, 0.47014206},
				{0.33333333, 0.11250000, 0.33333333}
				};
		return rlt;
	}
}
