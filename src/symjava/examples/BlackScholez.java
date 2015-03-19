package symjava.examples;

import symjava.domains.Domain;
import symjava.domains.Interval;
import symjava.symbolic.*;
import static symjava.symbolic.Symbol.*;
import static symjava.math.SymMath.*;

public class BlackScholez {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Symbol spot = new Symbol("spot");
		Symbol strike = new Symbol("strike");
		Symbol rd = new Symbol("rd");
		Symbol rf = new Symbol("rf");
		Symbol vol = new Symbol("\\sigma");
		Symbol tau = new Symbol("\\tau");
		Symbol phi = new Symbol("\\phi");
		
		Expr domDf = exp(-rd*tau); 
		Expr forDf = exp(-rf*tau);
		Expr fwd=spot*forDf/domDf;
		Expr stdDev=vol*sqrt(tau);

		Expr dp = (log(fwd/strike)+0.5*pow(stdDev,2))/stdDev;
		Expr dm = (log(fwd/strike)-0.5*pow(stdDev,2))/stdDev;
		System.out.println(dp);
		System.out.println(dm);

		Domain I1 = Interval.apply(-oo, phi*dp, z);
		Expr cdf1 = Integrate.apply(exp(-0.5*pow(z,2)), I1)/sqrt(PI2);
		Domain I2 = Interval.apply(-oo, phi*dm, z);
		Expr cdf2 = Integrate.apply(exp(-0.5*pow(z,2)), I2)/sqrt(PI2);
		
		Expr res = phi*domDf*(fwd*cdf1-strike*cdf2);
		System.out.println(res);
	}

}
