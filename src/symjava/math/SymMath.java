package symjava.math;

import symjava.matrix.SymVector;
import symjava.symbolic.Cos;
import symjava.symbolic.Exp;
import symjava.symbolic.Expr;
import symjava.symbolic.Log;
import symjava.symbolic.Log10;
import symjava.symbolic.Log2;
import symjava.symbolic.Pow;
import symjava.symbolic.Sin;
import symjava.symbolic.Sqrt;
import symjava.symbolic.SymConst;
import symjava.symbolic.Tan;

public class SymMath {
	/**
	 * Pre defined constant symbols
	 */
	public static SymConst PI = new SymConst("\\pi", Math.PI);
	public static SymConst PI2 = new SymConst("2\\pi", 2*Math.PI);
	public static SymConst E = new SymConst("e", Math.E);
	
	/**
	 * A quick way to define constant real number symbols
	 * @param v
	 * @return
	 */
	public static Expr C(double v) {
		return Expr.valueOf(v);
	}
	public static Expr C(float v) {
		return Expr.valueOf(v);
	}
	public static Expr C(int v) {
		return Expr.valueOf(v);
	}
	public static Expr C(long v) {
		return Expr.valueOf(v);
	}
	
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
	
	public static Expr dot(double[] l, SymVector r) {
		SymVector v = new SymVector(l, 0, r.dim());
		return Dot.apply(v, r);
	}

	public static Expr dot(SymVector l, double[] r) {
		SymVector v = new SymVector(r, 0, l.dim());
		return Dot.apply(l, v);
	}
	
//	public static Expr dot(double[] l, double[] r) {
//		double sum = 0.0;
//		for(int i=0; i<l.length; i++)
//			sum += l[i]*r[i];
//		return sum;
//	}
//	???
//	Exception in thread "main" java.lang.VerifyError: Bad type on operand stack
//	Exception Details:
//	  Location:
//	    symjava/math/SymMath.dot([D[D)Lsymjava/symbolic/Expr; @34: dreturn
//	  Reason:
//	    Type 'symjava/symbolic/Expr' (current frame, stack[0]) is not assignable to double_2nd
//	  Current Frame:
//	    bci: @34
//	    flags: { }
//	    locals: { '[D', '[D', double, double_2nd, integer }
//	    stack: { 'symjava/symbolic/Expr' }
//	  Bytecode:
//	    0000000: 0e49 0336 04a7 0012 282a 1504 312b 1504
//	    0000010: 316b 6349 8404 0115 042a bea1 ffed 28b8
//	    0000020: 002c af                                
//	  Stackmap Table:
//	    append_frame(@8,Double,Integer)
//	    same_frame(@23)
//
//		at symjava.examples.SVM.main(SVM.java:11)
		
	public static SymVector grad(Expr f) {
		return Grad.apply(f);
	}
	
}
