package test;
import static symbolic.Symbol.r;
import static symbolic.Symbol.s;
import static symbolic.Symbol.t;
import static symbolic.Symbol.x;
import static symbolic.Symbol.y;
import static symbolic.Symbol.z;

import java.util.ArrayList;
import java.util.List;

import symbolic.Expr;
import symbolic.Func;
import symbolic.Power;
import symbolic.Summation;
import symbolic.SymDouble;
import symbolic.SymFloat;
import symbolic.SymInteger;
import symbolic.SymLong;
import symbolic.Symbol;
import symbolic.Symbols;
import symbolic.Utils;
import bytecode.BytecodeFunc;


public class TestSymbolic {
	
	public static void testBasic() {
		Expr expr = x + y + z;
		System.out.println(expr);

		SymInteger n1 = new SymInteger(1);
		SymLong n2 = new SymLong(2);
		SymFloat n3 = new SymFloat(3.0f);
		SymDouble n4 = new SymDouble(4.0);
		System.out.println(n1 + n2 + n3 + n4);
		
		expr = r * x + s * y + t * z;
		System.out.println(expr);
		
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
	
	public static void testSimplify() {
		System.out.println("--------------testSimplify-----------------");
		Expr expr = x + y + z;
		Expr sub_expr = expr.subs(x, 1).subs(y, 2L).subs(z, 3.0d);
		System.out.println(sub_expr);
		System.out.println(sub_expr.simplify());
		
		Expr nxn = (x + 1) + 2; 
		System.out.println(nxn);
		
		Expr ypz = (y + z) + (y + 1);
		System.out.println(ypz);

		Expr ymz = (y * z) * (y * 2);
		System.out.println(ymz);
		
		Expr yz= y + z;
		sub_expr = expr.subs(x, yz);
		System.out.println(sub_expr.simplify());
	}
	
	public static void testToBytecodeFunc() {
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
	}
	
	public static void testDiff() {
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
		//eclipse���ܱ�������⣺cmd����ĳ��classĿ¼�󣬸�Ŀ¼������ɾ����
		//����eclipse����ɾ����Ŀ¼�����Բ��ܱ���
		testBasic();
		testSimplify();
		testToBytecodeFunc();
		testDiff();
	}
}
