package io.lambdacloud.symjava.examples;

import io.lambdacloud.symjava.relational.Eq;
import io.lambdacloud.symjava.symbolic.*;
import io.lambdacloud.symjava.symbolic.utils.AddList;
import io.lambdacloud.symjava.symbolic.utils.JIT;
import io.lambdacloud.symjava.symbolic.utils.Utils;
import static io.lambdacloud.symjava.math.SymMath.*;
import static io.lambdacloud.symjava.symbolic.Symbol.*;

public class QPSlover {

	public static void main(String[] args) {
		test0();
		test1();
		test2(); //?
		test3(); //?
	}
	
	public static void test0() {
		/**
		 * http://www.akiti.ca/QuadProgEx0Constr.html
				minimize F = (5/2)x1^2 -2x1x2 -x1x3 + 2x2^2 + 3x2x3 + (5/2)x3^2 + 2x1 - 35x2 - 47x3 + 5 
				True Solution: (x1,x2,x3) = (3,5,7)
		*/
		Symbol x1 = new Symbol("x_1");
		Symbol x2 = new Symbol("x_2");
		Symbol x3 = new Symbol("x_3");
		
		Expr F = 2.5*x1*x1 -2*x1*x2 -x1*x3 + 2*x2*x2 + 3*x2*x3 + 2.5*x3*x3 + 2*x1 - 35*x2 - 47*x3 + 5 ;
		System.out.println(F);
		
		Expr[] freeVars = new Expr[]{ x1, x2, x3 };
		Eq eq = new Eq(F, C0, freeVars);
		System.out.println(eq);
		
		double[] guess = new double[freeVars.length];
		NewtonOptimization.solve(eq, guess, 10, 1e-3, false);
		
		//Verifiy:
		double[][] A = {
				{ 5,-2,-1},
				{-2, 4, 3},
				{-1, 3, 5},
		};
		double[] b = {-2,35,47};
		double[] x = {0, 0, 0};
		Solver.solveCG2(A, b, x);
		for(double i : x)
			System.out.println(i);
	}
	
	public static void test1() {
/**
 * http://doc.cgal.org/latest/QP_solver/index.html
		minimize x^2 + 4(y-4)^2
		subject to
			x + y <= 7
		   -x+2y <=4 
			x>=0
			y>=0
			y<=4
			
		Solution (x,y)=(2,3)
*/
		Expr f = x*x + 4*(y-4)*(y-4);
		Symbols lmd = new Symbols("\\lambda");
		Symbols c = new Symbols("c");
		AddList addList = new AddList();
		addList.add(lmd[1]*( x+y-7+c[1]*c[1] ));
		addList.add(lmd[2]*(-x+2*y-4+c[2]*c[2] ));
		addList.add(lmd[3]*( x-c[3]*c[3] ));
		addList.add(lmd[4]*( y-c[4]*c[4] ));
		addList.add(lmd[5]*( y-4+c[5]*c[5] ));
		Expr L = f + addList.toExpr();
		System.out.println(L);
		
		Expr[] freeVars = Utils.joinArrays(new Expr[]{x, y}, lmd.get(1, 5), c.get(1, 5));
		Eq eq = new Eq(L, C0, freeVars);
		System.out.println(eq);
		
		double[] guess = new double[freeVars.length];
		NewtonOptimization.solve(eq, guess, 10, 1e-3, false);
		double xx = guess[0];
		double yy = guess[1];
		double objValue = JIT.compile(f).apply(xx,yy);
		System.out.println("Objective Value = "+objValue); //=8
	}
	
	public static void test2() {
		/**
		 * http://doc.cgal.org/latest/QP_solver/index.html
				minimize -32y+64
				subject to
					x + y <= 7
				   -x+2y <=4 
					x>=0
					y>=0
					y<=4

		Solution (x,y)=(10/3, 11/3)
		*/
		Expr f = -32*y+64;
		Symbols lmd = new Symbols("\\lambda");
		Symbols c = new Symbols("c");
		AddList addList = new AddList();
		addList.add(lmd[1]*( x+y-7+c[1]*c[1] ));
		addList.add(lmd[2]*(-x+2*y-4+c[2]*c[2] ));
		addList.add(lmd[3]*( x-c[3]*c[3] ));
		addList.add(lmd[4]*( y-c[4]*c[4] ));
		addList.add(lmd[5]*( y-4+c[5]*c[5] ));
		Expr L = f + addList.toExpr();
		System.out.println(L);
		
		Expr[] freeVars = Utils.joinArrays(new Expr[]{x, y}, lmd.get(1, 5), c.get(1, 5));
		Eq eq = new Eq(L, C0, freeVars);
		System.out.println(eq);
		
		double[] guess = new double[freeVars.length];
		NewtonOptimization.solve(eq, guess, 10, 1e-3, false);
		double xx = guess[0];
		double yy = guess[1];
		double objValue = JIT.compile(new Expr[]{x,y},f).apply(xx,yy);
		System.out.println("Objective Value = "+objValue); //=-160/3
	}
	
	public static void test3() {
		/**
		 * http://doc.cgal.org/latest/QP_solver/index.html
				minimize x^2 + 4(y-4)^2
				subject to
					x + y <= 7
				   -x+2y <=4 
					x>=0
					y>=0
					//y<=4
					
				Solution (x,y)=(2,3)
		*/
		Expr f = x*x + 4*(y-4)*(y-4);
		Symbols lmd = new Symbols("\\lambda");
		Symbols c = new Symbols("c");
		AddList addList = new AddList();
		addList.add(lmd[1]*( x+y-7+c[1]*c[1] ));
		addList.add(lmd[2]*(-x+2*y-4+c[2]*c[2] ));
		addList.add(lmd[3]*( x-c[3]*c[3] ));
		addList.add(lmd[4]*( y-c[4]*c[4] ));
		//addList.add(lmd[5]*( y-4+c[5]*c[5] ));
		Expr L = f + addList.toExpr();
		System.out.println(L);
		
		Expr[] freeVars = Utils.joinArrays(new Expr[]{x, y}, lmd.get(1, 4), c.get(1, 4));
		Eq eq = new Eq(L, C0, freeVars);
		System.out.println(eq);
		
		double[] guess = new double[freeVars.length];
		NewtonOptimization.solve(eq, guess, 10, 1e-3, false);
		double xx = guess[0];
		double yy = guess[1];
		double objValue = JIT.compile(new Expr[]{x,y},f).apply(xx,yy);
		System.out.println("Objective Value = "+objValue); //=8
	}
	


}
