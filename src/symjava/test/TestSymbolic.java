package symjava.test;

import static symjava.symbolic.Symbol.*;
import symjava.bytecode.BytecodeFunc;
import symjava.symbolic.*;
import symjava.symbolic.utils.BytecodeUtils;

import java.util.*;


public class TestSymbolic {
	public static void checkResult(String expect, Expr expr) {
		if( expect.equals(expr.toString()) )
			System.out.println(true);
		else
			System.out.println("FAIL: " + expect +" != " + expr);
	}
	public static void checkResult(Expr expr1, Expr expr2) {
		if( expr1.symEquals(expr2) )
			System.out.println(true);
		else
			System.out.println("FAIL: " + expr1 +" != " + expr2);
	}
	public static void checkResult(double d1, double d2, Expr expr) {
		if( d1 == d2 )
			System.out.println(true);
		else {
			if(expr != null)
				System.out.println("FAIL: " + expr + " => " + d1 +" != " + d2);
			else
				System.out.println("FAIL: " + d1 +" != " + d2);
		}
	}	
	public static void testBasic() {
		System.out.println("--------------testBasic-----------------");
		Expr expr = x + y;
		checkResult("x + y",expr);

		expr = x - y;
		checkResult("x - y",expr);

		expr = x * y;
		checkResult("x*y",expr);

		expr = x / y;
		checkResult("x/y",expr);
		
		expr = - x;
		checkResult("-x",expr);

		expr = new Power(x,2);
		checkResult("x^2",expr);

		checkResult("0.0", Symbol.Cm1 + Symbol.C1);
		checkResult("0", x + (-x));

		checkResult("-1", Symbol.Cm1 * Symbol.C1);
		checkResult("-x", x * -1);

		checkResult("1/x", new Reciprocal(x));
		checkResult("1/x", Symbol.C1 / x);
		checkResult("x/y", x * new Reciprocal(y));
		checkResult("1/(x*y)", new Reciprocal(x) * new Reciprocal(y));

		checkResult("r + x + y + z", (x + y) + (z + r));
		checkResult("r*x*y*z", (x * y) * (z * r));
		
		checkResult("x/(y*z)", x / (y * z));
		checkResult("x/(y*z)", x / y / z);
		checkResult("x + y/z", x + y / z);
		
		checkResult("r*x + s/y + t - z", r * x + s / y + t - z);

	}
	public static void testPrint() {
		System.out.println("--------------testPrint-----------------");
		checkResult("x*y + x*z", (x*(y+z)));
		checkResult("x/(y + z)", (x/(y+z)));
		checkResult("(y + z)^2", ((y+z)*(y+z)));
		checkResult("-(y + z)", (-(y+z)));
	}
	public static void testSimplify() {
		System.out.println("--------------testSimplify-----------------");
		Expr expr = null;
		
		SymInteger n1 = new SymInteger(1);
		SymLong n2 = new SymLong(2);
		SymFloat n3 = new SymFloat(3.0f);
		SymDouble n4 = new SymDouble(4.0);
		expr = n1 + n2 + n3 + n4;
		checkResult("10.0",expr);
		expr = n1 * n2 * n3 * n4;
		checkResult("24.0",expr);
		expr = n4 - n2;
		checkResult("2.0",expr);
		expr = n4 / n2;
		checkResult("2.0",expr);
		expr = n2 * (n1 + n3) / n4;
		checkResult("2.0",expr);
		
		checkResult(x + y + z, x + z + y);
		checkResult(x + y + z, y + x + z);
		checkResult(x + y + z, y + z + x);
		checkResult(x + y + z, z + x + y);
		checkResult(x + y + z, z + y + x);

		checkResult(x * y * z, x * z * y);
		checkResult(x * y * z, y * x * z);
		checkResult(x * y * z, y * z * x);
		checkResult(x * y * z, z * x * y);
		checkResult(x * y * z, z * y * x);

		
		checkResult("x^5*y*z", x * y * x * z * x * new Power(x,2));
		
		checkResult("2*(y*z)^2 + x^3*y*z", (x * y * x * z * x) + (y * z * y * z) + (z * y * z * y));
	
		checkResult("2*x*y*z + x^2*y + x*y^2 + x^2*z + x*z^2 + y^2*z + y*z^2", (x + y) * (y + z) * (z + x) );
		
		checkResult("x^2*y + x*z^2 + y^2*z", (x * y * x) + (y * z * y) + (z * x * z) );
		
		
		checkResult("x^2 - y^2", (x + y) * (x - y));
		checkResult("r*s*x*z + r*s*y*z", ((x + y) * z) * (r * s));
		checkResult(((x + y) * z) * (r * s), x*z*r*s + y*z*r*s);
	
		checkResult(x * y * z + r + s + t, r + s + t + z * y * x);
		
		expr = x + y + z;
		Expr sub_expr = expr.subs(x, 1).subs(y, 2L).subs(z, 3.0d);
		checkResult("1 + 2 + 3.0", sub_expr);
		checkResult("6.0", sub_expr.simplify());
		
		expr = (x + 1) + 2; 
		checkResult("3.0 + x", expr);
		
		expr = (y + z) + (y + 1);
		checkResult("1 + 2*y + z", expr);

		expr = (y * z) * (y * 2);
		checkResult("2*y^2*z", expr);
		
		expr = x + y + z;
		Expr yz= y + z;
		sub_expr = expr.subs(x, yz);
		checkResult("y + z + y + z", sub_expr);
		checkResult(sub_expr, (z + y) + (y + z));
		checkResult(sub_expr, (z + y)*2.0);

		checkResult(r * x + s / y + t - z, s / y + r * x + t - z);
		checkResult(r * x + s / y + t - z, r * x + t + s / y - z);
		checkResult(r * x + s / y + t - z, r * x + s / y - z + t);
		checkResult(r * x + s / y + t - z, t + r * x + s / y - z);
		
		checkResult(x * -(y + z), -x*y - x*z);
	}
	
	public static void testSummation() {
		System.out.println("--------------testSummation-----------------");
		Expr sum = new Summation( x*x, x, 1, 5);
		checkResult("\\Sigma_x=1^5(x^2)", sum);
		checkResult("\\Sigma_x=1^5((2)^2)", sum.subs(x, 2));
		
		Symbol i = new Symbol("i");
		Symbols ss = new Symbols("x", i);
		checkResult("x_i", ss);
		checkResult("x_2", ss.get(2));
		
		Summation sum2 = new Summation( ss*ss, i, 1, 5);
		checkResult("\\Sigma_i=1^5((x_i)^2)",sum2);
		
		for(int j=sum2.start; j<sum2.end; j++) {
			checkResult("x_"+j+"^2", sum2.getSummand(j));
		}
		Expr summand2 = sum2.getSummand(2).subs(ss.get(2), y);
		checkResult("y^2",summand2);
		
		int n = 100;
		Expr sum3 = new Summation(new Reciprocal((x+3.5)*(x+8)), x, 1, n);
		checkResult("\\Sigma_x=1^100(1/(28.0 + 11.5*x + x^2))", sum3);
		
	}
	
	public static void testToBytecodeFunc() {
		System.out.println("--------------testToBytecodeFunc-----------------");
		Expr expr = new Power(x + y * z, 2);
		checkResult("(x + y*z)^2", expr);
		
//		List<Expr> list = new ArrayList<Expr>();
//		BytecodeUtils.post_order(expr, list);
//		for(Expr e : list)
//			System.out.println(e.getClass());
		
		Func f = new Func("test_fun1",expr);
		checkResult("test_fun1(x,y,z)",f);
		
		BytecodeFunc func = f.toBytecodeFunc();
		checkResult(49.0, func.apply(1,2,3), null);
		
		Func c = new Func("test_const", new SymInteger(8));	
		func = c.toBytecodeFunc();
		checkResult(8.0, func.apply(0.0), null);
		
		Reciprocal rec = new Reciprocal(x + y);
		
		checkResult(0.5, new Func("test_reciprocal", rec).toBytecodeFunc().apply(1,1), rec);
		
	}
	
	public static void testDiff() {
		System.out.println("--------------testDiff-----------------");
		Expr expr = x*x*2.0 + x + 1.0;
		checkResult("1 + 4.0*x", expr.diff(x));
		
		Expr expr2 = -(x*x + y*y);
		checkResult("-2*x", expr2.diff(x));
		checkResult("-2*y", expr2.diff(y));
		
		Func f = new Func("test_fun2",expr2);
		checkResult("test_fun2(x,y)", f);
		checkResult(-13.0, f.toBytecodeFunc().apply(2,3), null);
	}
	
	public static void main(String[] args) {
		//eclipse不能编译的问题：cmd进到某个class目录后，该目录不允许删除，
		//导致eclipse不能删除该目录，所以不能编译
		testBasic();
		testPrint();
		testSimplify();
		testSummation();
		testToBytecodeFunc();
		testDiff();
	}
}
