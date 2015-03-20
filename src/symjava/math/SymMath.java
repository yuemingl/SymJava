package symjava.math;

import symjava.matrix.SymVector;
import symjava.symbolic.Cos;
import symjava.symbolic.Dot;
import symjava.symbolic.Exp;
import symjava.symbolic.Expr;
import symjava.symbolic.Grad;
import symjava.symbolic.Log;
import symjava.symbolic.Log10;
import symjava.symbolic.Log2;
import symjava.symbolic.Pow;
import symjava.symbolic.Sin;
import symjava.symbolic.Sqrt;
import symjava.symbolic.SymConst;
import symjava.symbolic.Tan;

public class SymMath {
	public static SymConst PI = new SymConst("\\pi", Math.PI);
	public static SymConst PI2 = new SymConst("2\\pi", 2*Math.PI);
	public static SymConst E = new SymConst("e", Math.E);
	
	public static Expr pow(Expr base, double exponent) {
		return Pow.simplifiedIns(base, Expr.valueOf(exponent));
	}
	public static Expr pow(double base, Expr exponent) {
		return Pow.simplifiedIns(Expr.valueOf(base), exponent);
	}
	
	public static Expr pow(Expr base, Expr exponent) {
		return Pow.simplifiedIns(base, exponent);
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

	public static Expr exp(Expr x) {
		return Exp.simplifiedIns(x);
	}

	public static Expr exp(double x) {
		return Exp.simplifiedIns(Expr.valueOf(x));
	}
	
	public static Expr log(Expr x) {
		return Log.simplifiedIns(x);
	}
	
	public static Expr log(Expr base, double expr) {
		return Log.simplifiedIns(base, Expr.valueOf(expr));
	}

	public static Expr log(double base, Expr expr) {
		return Log.simplifiedIns(Expr.valueOf(base), expr);
	}
	
	public static Expr log(Expr base, Expr expr) {
		return Log.simplifiedIns(base, expr);
	}

	public static Expr log10(Expr x) {
		return Log10.simplifiedIns(x);
	}
	
	public static Expr log2(Expr x) {
		return Log2.simplifiedIns(x);
	}
	
	///////////////////////////////////////////////////////////////
	
	public static Expr sin(Expr x) {
		return Sin.simplifiedIns(x);
	}

	public static Expr cos(Expr x) {
		return Cos.simplifiedIns(x);
	}

	public static Expr tan(Expr x) {
		return Tan.simplifiedIns(x);
	}
	
	//////////////////////////////////////////////////////////////
	
	public static Expr dot(SymVector l, SymVector r) {
		return Dot.apply(l, r);
	}
	
	public static SymVector grad(Expr f) {
		return Grad.apply(f);
	}
	
}
