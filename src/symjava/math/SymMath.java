package symjava.math;

import symjava.matrix.SymVector;
import symjava.symbolic.Dot;
import symjava.symbolic.Expr;
import symjava.symbolic.Grad;
import symjava.symbolic.Power;
import symjava.symbolic.Sqrt;

public class SymMath {
	public static Expr pow(Expr base, int exponent) {
		return Power.simplifiedIns(base, exponent);
	}
	
	public static Expr dot(SymVector l, SymVector r) {
		return Dot.apply(l, r);
	}
	
	public static SymVector grad(Expr f) {
		return Grad.apply(f);
	}
	
	public static Expr sqrt(Expr arg) {
		return new Sqrt(arg);
	}
}
