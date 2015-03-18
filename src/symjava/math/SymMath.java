package symjava.math;

import symjava.matrix.SymVector;
import symjava.symbolic.Cos;
import symjava.symbolic.Dot;
import symjava.symbolic.Expr;
import symjava.symbolic.Grad;
import symjava.symbolic.Pow;
import symjava.symbolic.Sin;
import symjava.symbolic.Sqrt;
import symjava.symbolic.Tan;

public class SymMath {
	public static Expr pow(Expr base, double exponent) {
		return Pow.simplifiedIns(base, Expr.valueOf(exponent));
	}
	public static Expr pow(double base, Expr exponent) {
		return Pow.simplifiedIns(Expr.valueOf(base), exponent);
	}
	
	public static Expr pow(Expr base, Expr exponent) {
		return Pow.simplifiedIns(base, exponent);
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
	
	public static Expr sqrt(Expr arg, double root) {
		return new Sqrt(arg, Expr.valueOf(root));
	}
	
	public static Expr sqrt(Expr arg, Expr root) {
		return Sqrt.simplifiedIns(arg, root);
	}
	
	public static Expr sin(Expr x) {
		return Sin.simplifiedIns(x);
	}

	public static Expr cos(Expr x) {
		return Cos.simplifiedIns(x);
	}

	public static Expr tan(Expr x) {
		return Tan.simplifiedIns(x);
	}
}
