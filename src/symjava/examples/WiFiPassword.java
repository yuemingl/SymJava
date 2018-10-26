package symjava.examples;

import static symjava.math.SymMath.*;
import static symjava.symbolic.Symbol.x;
import symjava.bytecode.BytecodeFunc;
import symjava.domains.Domain;
import symjava.domains.Interval;
import symjava.symbolic.Expr;
import symjava.symbolic.Integrate;
import symjava.symbolic.utils.JIT;

public class WiFiPassword {
	public static void password() {
		//Define the interval
		Domain I = Interval.apply(-5, 5).setStepSize(0.01);
		
		//Define the integral
		Expr f = Integrate.apply(
				(pow(x,3)*pow(sin(x),2))/(pow(x,4)+2*pow(x,2)+1), I);
		System.out.println(f);
		
		//Compile f to perform numerical integration
		BytecodeFunc cf = JIT.compile(f);
		System.out.println(String.format("%.3f", cf.apply()));
	}
	
	public static void password2() {
		// Define the interal
		Domain I = Interval.apply(-2, 2).setStepSize(0.00001);

		// Define the integral
		Expr i = Integrate.apply((pow(x, 3) * cos(x / 2.0) + 0.5)
				* sqrt(4 - x * x), I);
		System.out.println(i);

		// Compile the integral to perform numerical integration
		BytecodeFunc f = JIT.compile(i);
		System.out.println(f.apply());
	}
	public static void main(String[] args) {
		//password();
		password2();
	}
}
