package symjava.test;

import symjava.bytecode.BytecodeFunc;
import symjava.symbolic.Expr;
import symjava.symbolic.Func;
import static symjava.symbolic.Symbol.*;
import static symjava.math.SymMath.*;

public class Benchmark {

	public static void main(String[] args) {
		Expr expr = 2.0 * pow(x, 5) + 3.0 * x * y;
		System.out.println(expr);
		
		Func fun = new Func("fun", expr);
		BytecodeFunc bfun = fun.toBytecodeFunc();
		System.out.println(bfun.apply(2.0, 3.0));
		
		System.out.println(expr.subs(x, 2.0).subs(y, 3.0).simplify());
		System.out.println(expr.subs(x, 2.0).subs(y, 3.0).simplify());
		
		int N = 1000000;
		long start, end;
		
		System.out.println("Run "+N+" times:");
		start = System.currentTimeMillis();
		for(int i=0; i<N; i++)
			bfun.apply(2.0, 3.0);
		end = System.currentTimeMillis();
		System.out.println("bytecode fun: "+(end-start)+"ms");
		
		start = System.currentTimeMillis();
		for(int i=0; i<N; i++)
			expr.subs(x, 2.0).subs(y, 3.0);
		end = System.currentTimeMillis();
		System.out.println("expr subs: "+(end-start)+"ms");
		
		
	}

}
