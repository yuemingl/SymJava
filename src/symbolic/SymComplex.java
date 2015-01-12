package symbolic;

public class SymComplex extends Expr {
	Expr real;
	Expr imaginary;
	
	public SymComplex(Expr re, Expr im) {
		real = re;
		imaginary = im;
	}
	
	@Override
	public Expr diff(Expr expr) {
		return new SymComplex(real.diff(expr), imaginary.diff(expr));
	}
	
	public String toString() {
		return real.name + "+" + imaginary.name + "i";
	}

	@Override
	public Expr subs(Expr from, Expr to) {
		return new SymComplex(real.subs(from, to), imaginary.subs(from, to));
	}

	@Override
	public Expr simplify() {
		return new SymComplex(real.simplify(), imaginary.simplify());
	}

}
