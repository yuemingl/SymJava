package symjava.symbolic;

import symjava.symbolic.utils.Utils;

public class Symbol extends Expr {
	public static Symbol a = new Symbol("a");
	public static Symbol b = new Symbol("b");
	public static Symbol c = new Symbol("c");
	public static Symbol d = new Symbol("d");
	public static Symbol e = new Symbol("e");
	public static Symbol f = new Symbol("f");
	public static Symbol g = new Symbol("g");
	public static Symbol h = new Symbol("h");
	
	public static Symbol r = new Symbol("r");
	public static Symbol s = new Symbol("s");
	public static Symbol t = new Symbol("t");
	
	public static Symbol u = new Symbol("u");
	public static Symbol v = new Symbol("v");
	public static Symbol w = new Symbol("w");
	
	public static Symbol x = new Symbol("x");
	public static Symbol y = new Symbol("y");
	public static Symbol z = new Symbol("z");
	
	public static Symbol phi = new Symbol("\\phi");
	public static Symbol psi = new Symbol("\\psi");
	public static Symbol chi = new Symbol("\\chi");
	
	public static Symbol alpha = new Symbol("\\alpha");
	public static Symbol beta = new Symbol("\\beta");
	public static Symbol gamma = new Symbol("\\gamma");
	
	public static SymInteger Cm2 = new SymInteger(-1);
	public static SymInteger Cm1 = new SymInteger(-1);
	public static SymInteger C0 = new SymInteger(0);
	public static SymInteger C1 = new SymInteger(1);
	public static SymInteger C2 = new SymInteger(2);
	
	public static Infinity oo = new Infinity();
	
	
	public Symbol(String name) {
		this.label = name;
		sortKey = label;
	}
	
	public String toString() {
		return label;
	}
	
	@Override
	public Expr subs(Expr from, Expr to) {
		if(Utils.symCompare(this, from)) {
			return to;
		}
		return this;
	}

	@Override
	public Expr diff(Expr expr) {
		if(this.symEquals(expr))
			return C1;
		return C0;
	}

	@Override
	public Expr simplify() {
		return this;
	}

	@Override
	public boolean symEquals(Expr other) {
		if(other instanceof Symbol)
			return this.label.equals(other.label);
		return false;
	}
	
	public boolean containsSubIndex() {
		String[] ss = this.toString().split("_");
		if(ss.length == 2) {
			Integer.valueOf(ss[1]);
			return true;
		}
		return false;
	}
	
	public int getSubIndex() {
		String[] ss = this.toString().split("_");
		if(ss.length == 2) {
			return Integer.valueOf(ss[1]);
		}
		throw new IllegalArgumentException(this+" contains no sub index, use containSubIndex() to check.");
	}
}
