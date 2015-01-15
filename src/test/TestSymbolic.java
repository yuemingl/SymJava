package test;

import static symbolic.Symbol.*;
import symbolic.*;
import bytecode.BytecodeFunc;
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
		else
			System.out.println("FAIL: " + expr + " => " + d1 +" != " + d2);
	}	
	public static void testBasic() {
		System.out.println("--------------testBasic-----------------");
		Expr expr = x + y;
		checkResult("x + y",expr);

		expr = x - y;
		checkResult("x - y",expr);

		expr = x * y;
		checkResult("x * y",expr);

		expr = x / y;
		checkResult("x / y",expr);
		
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
		checkResult("x / y", x * new Reciprocal(y));
		checkResult("1/(x * y)", new Reciprocal(x) * new Reciprocal(y));

		checkResult("x + y / z", x + y / z);
		
		checkResult("r * x + s / y + t - z", r * x + s / y + t - z);

	}
	public static void testPrint() {
		System.out.println("--------------testPrint-----------------");
		checkResult("x * (y + z)", (x*(y+z)));
		checkResult("x / (y + z)", (x/(y+z)));
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

		
		checkResult("", x * y * x * z * x * new Power(x,2));
		
		checkResult("", (x * y * x * z * x) + (y * z * y * z) + (z * y * z * y));
	
		checkResult("", (x + y) * (y + z) * (z + x) );
		
		checkResult("", (x * y * x) + (y * z * y) + (z * x * z) );
		
		
		checkResult("", ((x + y) * z) * (r * s));
		checkResult(((x + y) * z) * (r * s), x*z*r*s + y*z*r*s);
	
		checkResult(x * y * z + r + s + t, r + s + t + z * y * x);
		
		expr = x + y + z;
		Expr sub_expr = expr.subs(x, 1).subs(y, 2L).subs(z, 3.0d);
		checkResult("1 + 2 + 3.0", sub_expr);
		checkResult("6.0", sub_expr.simplify());
		
		expr = (x + 1) + 2; 
		checkResult("3.0 + x", expr);
		
		expr = (y + z) + (y + 1);
		checkResult("1 + 2 * y + z", expr);

		expr = (y * z) * (y * 2);
		checkResult("2 * y^2 * z", expr);
		
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
		System.out.println(sum);
		System.out.println(sum.subs(x, 2));
		
		Symbol i = new Symbol("i");
		Symbols ss = new Symbols("x", i);
		System.out.println(ss);
		System.out.println(ss.get(2));
		
		Summation sum2 = new Summation( ss*ss, i, 1, 5);
		System.out.println(sum2);
		
		for(int j=sum2.start; j<sum2.end; j++) {
			System.out.println("summand_"+j+"="+sum2.getSummand(j));
		}
		System.out.println();
		Expr summand2 = sum2.getSummand(2).subs(ss.get(2), y);
		System.out.println(summand2);		
	}
	
	public static void testToBytecodeFunc() {
		System.out.println("--------------testToBytecodeFunc-----------------");
		Expr expr = new Power(x + y * z, 2);
		System.out.println(expr);
		
		List<Expr> list = new ArrayList<Expr>();
		Utils.post_order(expr, list);
		for(Expr e : list)
			System.out.println(e.getClass());
		
		Func f = new Func("test_fun",expr);
		System.out.println(f);
		
		BytecodeFunc func = f.toBytecodeFunc();
		System.out.println(func.apply(1,2,3));
		
		Func c = new Func("test_const", new SymInteger(8));
		list.clear();
		Utils.post_order(c.expr, list);
		for(Expr e : list)
			System.out.println(e.getClass());		
		func = c.toBytecodeFunc();
		System.out.println(func.apply(0.0));
		
		Reciprocal rec = new Reciprocal(x + y);
		
		checkResult(0.5, new Func("test_reciprocal", rec).toBytecodeFunc().apply(1,1), rec);
		
	}
	
	public static void testDiff() {
		System.out.println("--------------testDiff-----------------");
		Expr expr = x*x*2.0 + x + 1.0;
		System.out.println(expr.diff(x));
		
		Expr expr2 = -(x*x + y*y);
		System.out.println(expr2.diff(x));
		System.out.println(expr2.diff(y));
		
		Func f = new Func("test_fun2",expr2);
		System.out.println(f);
		System.out.println(f.toBytecodeFunc().apply(2,3));
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
