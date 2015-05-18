package symjava.examples;

import symjava.bytecode.BytecodeFunc;
import symjava.domains.Domain;
import symjava.domains.Interval;
import symjava.relational.Eq;
import symjava.symbolic.*;
import symjava.symbolic.utils.JIT;
import static symjava.symbolic.Symbol.*;
import static symjava.math.SymMath.*;

public class BlackScholes {

	public static void main(String[] args) {
		test1(); //Example from QuantLib
		test2(); //Example from UCLA Statistics C183/C283: Statistical Models in Finance
		peper_example();
	}
	
	
	public static void test1() {
		//http://quantlib.org/docs.shtml
		//Dimitri Reiswich contributed the slides he used during a course he taught, along with the corresponding code
		//http://quantlib.org/slides/dima-ql-intro-1.pdf
		//http://quantlib.org/slides/dima-ql-intro-2.pdf
		//http://quantlib.org/slides/dima-code.zip
		
		// Define symbols to construct the Black-Scholes formula
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

		//we use -10 instead of -oo for numerical computation
		double step = 1e-3;
		Domain I1 = Interval.apply(-10, phi*dp, z).setStepSize(step); 
		Domain I2 = Interval.apply(-10, phi*dm, z).setStepSize(step); 
		Expr cdf1 = Integrate.apply(exp(-0.5*pow(z,2)), I1)/sqrt(PI2);
		Expr cdf2 = Integrate.apply(exp(-0.5*pow(z,2)), I2)/sqrt(PI2);
		
		Expr res = phi*domDf*(fwd*cdf1-strike*cdf2);
		System.out.println("Balck-Scholez equation:");
		System.out.println(res+"\n");
		System.out.println("The first derivative of Balck-Scholez equation with respect to volatility \\sigma:");
		System.out.println(res.diff(vol)+"\n"); //Let computer do it for us.
		
		// Calculate Black-Scholes price for a given volatility: \sigma=0.1423
		BytecodeFunc blackScholesPrice = JIT.compile(new Expr[]{spot, strike, rd, rf, vol, tau, phi}, res);
		double price = blackScholesPrice.apply(100.0, 110.0, 0.002, 0.01, 0.1423, 0.5, 1);
		System.out.println(price);
		
		System.out.println("Use Newtom method to recover the volatility by giving the market data:");
		Expr[] freeVars = {vol};
		Expr[] params = {spot, strike, rd, rf, tau, phi}; //Specify the params in the given order
		Eq[] eq = new Eq[] {
			new Eq(res-price, C0, freeVars, params)
		};
		
		// Use Newton's method to find the root
		double[] guess = new double[]{ 0.10 };
		double[] constParams = new double[] {100.0, 110.0, 0.002, 0.01, 0.5, 1};
		Newton.solve(eq, guess, constParams, 100, 1e-5);
	}
	
	public static void test2() {
		//UCLA Statistics C183/C283: Statistical Models in Finance
		//http://www.stat.ucla.edu/~nchristo/statistics_c183_c283/
		//http://www.stat.ucla.edu/~nchristo/statistics_c183_c283/statc183c283_ito_black_scholes.pdf
		//http://www.stat.ucla.edu/~nchristo/statistics_c183_c283/statc183c283_implied_volatility.pdf
		/*
		R Example:
			Suppose the value of a European call is C = 1.875 when s0 = 21, E = 20, r = 0.1, t = 0.25. Use the method of Newton-Raphson
			to compute the implied volatility:
			#Inputs:
			s0 <- 21
			E <- 20
			r <- 0.1
			t <- 0.25
			c <- 1.875
			#Initial value of volatility:
			sigma <- 0.10
			sig <- rep(0,10)
			sig[1] <- sigma
			#Newton-Raphson method:
			for(i in 2:100){
			d1 <- (log(s0/E)+(r+sigma^2/2)*t)/(sigma*sqrt(t))
			d2 <- d1-sigma*sqrt(t)
			f <- s0*pnorm(d1)-E*exp(-r*t)*pnorm(d2)-c
			#Derivative of d1 w.r.t. sigma:
			d11 <- (sigma^2*t*sqrt(t)-(log(s0/E)+(r+sigma^2/2)*t)*sqrt(t))/(sigma^2*t)
			#Derivative of d2 w.r.t. sigma:
			d22 <- d11-sqrt(t)
			#Derivative of f(sigma):
			f1 <- s0*dnorm(d1)*d11-E*exp(-r*t)*dnorm(d2)*d22
			#Update sigma:
			sigma <- sigma - f/f1
			sig[i] <- sigma
			if(abs(sig[i]-sig[i-1]) < 0.00000001){sig<- sig[1:i]; break}
			}
			Here is the vector that contains the volatility at each step:
			> sig
			[1] 0.1000000 0.3575822 0.2396918 0.2345343 0.2345129 0.2345129
			The implied volatility is σ = 0.2345.
		*/
		
		
		Symbol s0 = new Symbol("s0"); //spot price
		Symbol E = new Symbol("E"); //strike price
		Symbol r = new Symbol("r"); //risk free rate
		Symbol sigma = new Symbol("\\sigma"); //volatility
		Symbol t = new Symbol("t");
		Symbol c = new Symbol("c");
		
		Expr d1 = (log(s0/E)+(r+pow(sigma,2)/2)*t)/(sigma*sqrt(t));
		Expr d2 = d1-sigma*sqrt(t);
		
		//we use -10 instead of -oo for numerical computation
		double step = 1e-3;
		Domain I1 = Interval.apply(-10, d1, z).setStepSize(step); 
		Domain I2 = Interval.apply(-10, d2, z).setStepSize(step); 
		Expr cdf1 = Integrate.apply(exp(-0.5*pow(z,2)), I1)/sqrt(PI2);
		Expr cdf2 = Integrate.apply(exp(-0.5*pow(z,2)), I2)/sqrt(PI2);
		Expr f = s0*cdf1-E*exp(-r*t)*cdf2-c;
		System.out.println("Balck-Scholez equation:");
		System.out.println(f+"\n");
		System.out.println("The first derivative of Balck-Scholez equation with respect to volatility \\sigma:");
		System.out.println(f.diff(sigma)+"\n"); //Let computer do it for us.

		Expr[] freeVars = {sigma};
		Expr[] params = {s0, E, r, t, c}; //Specify the params in the given order
		Eq[] eq = new Eq[] {
				Eq.apply(f, C0, freeVars, params)
			};
		double[] guess = new double[]{ 0.10 };
		double[] constParams = new double[] {21, 20, 0.1, 0.25, 1.875};
		Newton.solve(eq, guess, constParams, 100, 1e-5);
	}
	
	
	public static void peper_example() {
		// Define symbols to construct the Black-Scholes formula
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
		//We use -10 instead of -oo for numerical computation
		double step = 1e-3;
		Domain I1 = Interval.apply(-10, phi*(log(fwd/strike)+0.5*pow(stdDev,2))/stdDev, z)
				.setStepSize(step); 
		Domain I2 = Interval.apply(-10, phi*(log(fwd/strike)-0.5*pow(stdDev,2))/stdDev, z)
				.setStepSize(step); 
		Expr cdf1 = Integrate.apply(exp(-0.5*pow(z,2)), I1)/sqrt(PI2);
		Expr cdf2 = Integrate.apply(exp(-0.5*pow(z,2)), I2)/sqrt(PI2);
		Expr res = phi*domDf*(fwd*cdf1-strike*cdf2);
		//Use Newtom method to recover the volatility by giving the market data
		Expr[] freeVars = {vol};
		Expr[] params = {spot, strike, rd, rf, tau, phi};
		Eq[] eq = new Eq[] { new Eq(res-0.897865, C0, freeVars, params) };
		// Use Newton's method to find the root
		double[] guess = new double[]{ 0.10 };
		double[] constParams = new double[] {100.0, 110.0, 0.002, 0.01, 0.5, 1};
		Newton.solve(eq, guess, constParams, 100, 1e-5);
	}
}
