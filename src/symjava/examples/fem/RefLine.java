package symjava.examples.fem;

import symjava.domains.Domain1D;
import symjava.symbolic.Expr;
import static symjava.symbolic.Symbol.Cm1;
import static symjava.symbolic.Symbol.C1;

public class RefLine extends Domain1D {
	static double[][] order1 = {
		{0.0, 2.0}
	};
	
	static double[][] order2 = {
		{ -0.577350269189626, 0.577350269189626 },
		{ 1.0,                1.0               }
	};
	
	static double[][] order3 = {
		{ -0.774596669241483, 0.555555555555556 },
		{  0.0              , 0.888888888888889 },
		{  0.774596669241483, 0.555555555555556 }
	};
	
	static double[][] order4 = {
		{ -0.861136311594953, 0.347854845137454 },
		{ -0.339981043584856, 0.652145154862546 },
		{  0.339981043584856, 0.652145154862546 },
		{  0.861136311594953, 0.347854845137454 }
	};
	
	static double[][] order5 = {
		{  0.0              , 0.568888888888889 },
		{  0.538469310105683, 0.478628670499366 },
		{ -0.538469310105683, 0.478628670499366 },
		{  0.906179845938664, 0.236926885056189 },
		{ -0.906179845938664, 0.236926885056189 }
		
	};
	
	public RefLine(String label, Expr coordVar) {
		super(label, coordVar);
	}
	
	@Override
	public double[][] getIntWeightAndPoints(int order) {
		if(order == 3)
			return order3;
		if(order == 5)
			return order3;
		if(order == 4)
			return order3;
		if(order == 2)
			return order3;
		if(order == 1)
			return order3;
		return null;
	}
}
