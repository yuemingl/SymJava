package symjava.examples.fem;

import java.util.List;

import symjava.domains.Domain2D;
import symjava.symbolic.Expr;

public class RefTriangle extends Domain2D {
	static double[][] order3 = {
		{0.5, 0.5, 1.0 / 6.0},
		{0.0, 0.5, 1.0 / 6.0},
		{0.5, 0.0, 1.0 / 6.0}
	};
	
	static double[][] order4 = {
		{    0.6,     0.2,  25.0/96.0},
		{    0.2,     0.6,  25.0/96.0},
		{    0.2,     0.2,  25.0/96.0},
		{1.0/3.0, 1.0/3.0, -27.0/96.0}
	};
	
	static double[][] order5 = {
		{0.10128651, 0.10128651, 0.06296959},
		{0.47014206, 0.05971587, 0.06619708},
		{0.79742699, 0.10128651, 0.06296959},
		{0.47014206, 0.47014206, 0.06619708},
		{0.10128651, 0.79742699, 0.06296959},
		{0.05971587, 0.47014206, 0.06619708},
		{0.33333333, 0.33333333, 0.11250000}
	};
			
	public RefTriangle(String label, Expr ...coordVars) {
		super(label, coordVars);
	}
	
	public RefTriangle(String label, List<Expr> coordVars) {
		super(label, coordVars.toArray(new Expr[0]));
	}
	
	@Override
	public double[][] getIntWeightAndPoints(int order) {
		//The total weight is 0.5, since the area of the triangle is 0.5
		if(order == 3)
			return order3;
		if(order == 4)
			return order4;
		if(order == 5)
			return order5;
		return null;
	}
}
