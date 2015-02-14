package symjava.examples;

import symjava.domains.Domain;
import symjava.domains.Domain2D;
import symjava.domains.Domain3D;
import symjava.math.SymMath;
import symjava.math.Transformation;
import symjava.relational.Eq;
import symjava.symbolic.*;
import static symjava.symbolic.Symbol.*;

public class Example5 {

	public static void main(String[] args) {
		example1();
		example2();
	}
	
	public static void example1() {
		Domain D = new Domain2D("D",x,y);
		Int I1 = new Int(0.5*(x+y), D);
		System.out.println(I1);
		
		Transformation trans = new Transformation(
				new Eq(x, r/2),
				new Eq(y, 3*s)
				);
		Expr I1c = I1.changeOfVars(trans);
		System.out.println(I1c);
	}
	
	public static void example2() {
		Domain D = new Domain3D("D",x,y,z);
		Func F = new Func("F", r, s, t);
		Func G = new Func("G", r, s, t);
		Func H = new Func("H", r, s, t);
		Transformation trans = new Transformation(
				new Eq(x, F),
				new Eq(y, G),
				new Eq(z, H)
				);
		Int I = new Int(SymMath.pow(x+y+z,2), D).changeOfVars(trans);
		System.out.println(I);
	}
	
}
