package symjava.examples;

import static symjava.math.SymMath.pow;
import static symjava.math.SymMath.sin;
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
	public static void main(String[] args) {
		password();
	}
}
