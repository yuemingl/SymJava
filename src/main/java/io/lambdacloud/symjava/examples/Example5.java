package io.lambdacloud.symjava.examples;

import io.lambdacloud.symjava.domains.Domain;
import io.lambdacloud.symjava.domains.Domain2D;
import io.lambdacloud.symjava.domains.Domain3D;
import io.lambdacloud.symjava.math.SymMath;
import io.lambdacloud.symjava.math.Transformation;
import io.lambdacloud.symjava.relational.Eq;
import io.lambdacloud.symjava.symbolic.*;
import static io.lambdacloud.symjava.symbolic.Symbol.*;

public class Example5 {

	public static void main(String[] args) {
		example1();
		example2();
	}
	
	public static void example1() {
		Domain D = new Domain2D("D",x,y);
		Integrate I1 = new Integrate(0.5*(x+y), D);
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
		Integrate I = new Integrate(SymMath.pow(x+y+z,2), D).changeOfVars(trans);
		System.out.println(I);
	}
	
}
