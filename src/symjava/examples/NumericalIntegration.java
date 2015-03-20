package symjava.examples;

import static symjava.math.SymMath.PI;
import static symjava.math.SymMath.exp;
import static symjava.math.SymMath.pow;
import static symjava.math.SymMath.sqrt;
import static symjava.symbolic.Symbol.x;
import symjava.bytecode.BytecodeFunc;
import symjava.domains.Domain;
import symjava.domains.Interval;
import symjava.symbolic.Expr;
import symjava.symbolic.Integrate;
import symjava.symbolic.utils.JIT;

public class NumericalIntegration {

	public static void main(String[] args) {
		//Define the interal
		Domain I = Interval.apply(-10, 0).setStep(0.01);
		//Define the integral: cumulative distribution function
		Expr cdf = Integrate.apply(exp(-0.5*pow(x,2))/sqrt(2*PI), I);
		System.out.println(cdf); //\int_{-10.0}^{10.0}{1/\sqrt{2*\pi}*e^{-0.5*x^2}}dx
		
		//Compile cdf to perform numerical integration
		BytecodeFunc f = JIT.compile(cdf);
		System.out.println(f.apply()); //1.0
	}

}
