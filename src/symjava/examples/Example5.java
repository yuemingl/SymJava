package symjava.examples;

import symjava.math.SymMath;
import symjava.math.Transformation;
import symjava.relational.Eq;
import symjava.symbolic.*;
import symjava.symbolic.utils.Utils;
import static symjava.symbolic.Symbol.*;
import static symjava.math.SymMath.*;

public class Example5 {

	public static void main(String[] args) {
		//example1();
		//example2();
		example3();
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
	
	public static void example3() {
		
		Domain D = new Domain2D("D",x,y);
		
		//Create coordinate transformation
		SymConst x1 = new SymConst("x1");
		SymConst x2 = new SymConst("x2");
		SymConst x3 = new SymConst("x3");
		SymConst y1 = new SymConst("y1");
		SymConst y2 = new SymConst("y2");
		SymConst y3 = new SymConst("y3");
		Transformation trans = new Transformation(
				new Eq(x, x1*r+x2*s+x3*(1-r-s)),
				new Eq(y, y1*r+y2*s+y3*(1-r-s))
				);
		
		//Shape functions
		Func N1 = new Func("R", x, y);
		Func N2 = new Func("S", x, y);
		Func N3 = new Func("T", 1 - N1 - N2);
		Func[] shapeFuns = {N1, N2, N3};
		for(int i=0; i<shapeFuns.length; i++) {
			for(int j=0; j<shapeFuns.length; j++) {
				Func U = shapeFuns[i];
				Func V = shapeFuns[j];
				//Int I = new Int(dot(grad(U), grad(V)) + U*V, D).changeOfVars(trans);
				Int I = new Int(U*V, D).changeOfVars(trans);
				System.out.println(I.integrand);
				Expr tmp = I.integrand.subs(N1, r);
				tmp = tmp.subs(N2, s);
				tmp = tmp.subs(N3, t);
				System.out.println();
				System.out.println(tmp);
				System.out.println();
				System.out.println();
				
			}
		}
		
	}
	
}
