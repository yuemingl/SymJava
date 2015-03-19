package symjava.examples;

import symjava.bytecode.BytecodeFunc;
import symjava.domains.Domain;
import symjava.domains.Interval;
import symjava.relational.Eq;
import symjava.symbolic.*;
import symjava.symbolic.utils.JIT;
import static symjava.symbolic.Symbol.*;
import static symjava.math.SymMath.*;

public class BlackScholez {

	public static void main(String[] args) {
		//Construct the Balck-Scholez equation
		Symbol spot = new Symbol("spot"); //spot price
		Symbol strike = new Symbol("strike"); //strike price
		Symbol rd = new Symbol("rd");
		Symbol rf = new Symbol("rf");
		Symbol vol = new Symbol("\\sigma"); //volatility
		Symbol tau = new Symbol("\\tau");
		Symbol phi = new Symbol("\\phi");
		
		Expr domDf = exp(-rd*tau); 
		Expr forDf = exp(-rf*tau);
		Expr fwd=spot*forDf/domDf;
		Expr stdDev=vol*sqrt(tau);

		Expr dp = (log(fwd/strike)+0.5*pow(stdDev,2))/stdDev;
		Expr dm = (log(fwd/strike)-0.5*pow(stdDev,2))/stdDev;

		//Domain I1 = Interval.apply(-oo, phi*dp, z);
		Domain I1 = Interval.apply(-10, phi*dp, z); //Good enough for -10, it will take a long time to use -oo
		I1.setStep(1e-5);
		Expr cdf1 = Integrate.apply(exp(-0.5*pow(z,2)), I1)/sqrt(PI2);
		
		//Domain I2 = Interval.apply(-oo, phi*dm, z);
		Domain I2 = Interval.apply(-10, phi*dm, z);
		I2.setStep(1e-5);
		Expr cdf2 = Integrate.apply(exp(-0.5*pow(z,2)), I2)/sqrt(PI2);
		
		Expr res = phi*domDf*(fwd*cdf1-strike*cdf2);
		System.out.println("Balck-Scholez equation:");
		System.out.println(res+"\n");
		System.out.println("The first derivative of Balck-Scholez equation with respect to volatility \\sigma:");
		System.out.println(res.diff(vol)+"\n"); //Let computer do it for us.
		
		// Calculate Black-Scholes price for a given volatility: \sigma=0.1423
		BytecodeFunc blackScholesPrice = JIT.compile(new Expr[]{spot, strike, rd, rf, vol, tau, phi}, res);
		double price = blackScholesPrice.apply(100.0, 110.0, 0.002, 0.01, 0.1423, 0.5, 1);
		
		System.out.println("Use Newtom method to recover the volatility by given the market data:");
		Expr[] freeVars = {vol};
		Expr[] params = {spot, strike, rd, rf, tau, phi}; //Specify the params in the given order
		Eq[] eq = new Eq[] {
			new Eq(res-price, C0, freeVars, params)
		};
		
		double[] guess = new double[]{ 0.10 };
		double[] constParams = new double[] {100.0, 110.0, 0.002, 0.01, 0.5, 1};
		Newton.solve(eq, guess, constParams, 100, 1e-5);
	}

}
