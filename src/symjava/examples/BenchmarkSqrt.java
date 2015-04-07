package symjava.examples;

import static symjava.symbolic.Symbol.x;
import static symjava.math.SymMath.*;

import java.util.ArrayList;

import symjava.bytecode.BytecodeFunc;
import symjava.symbolic.Expr;
import symjava.symbolic.Func;

public class BenchmarkSqrt {
	public static double factorial(int n) {
		double rlt = 1;
		for(int i=1; i<=n; i++)
			rlt *= i;
		return rlt;
	}
	public static void main(String[] args) {
		int n = 9;
		Expr expr = 0;
		
		Expr term;
		ArrayList<Expr> exprs = new ArrayList<Expr>();
		for(int i=1; i<=n; i++) {
			term = (pow(x, 1.0/i));
			//term = (sqrt(x, i));
			//System.out.println(term);
			expr = expr + term;
			exprs.add(expr);
		}
		
		ArrayList<BytecodeFunc> funcs = new ArrayList<BytecodeFunc>();
		for(int i=0; i<n; i++) {
			Func func = new Func("func"+i, exprs.get(i));
			BytecodeFunc bfunc = func.toBytecodeFunc();
			System.out.println(bfunc.apply(0.1));
			funcs.add(bfunc);
		}
		
		int N=10000000;
		double x = 0.1;
		double out = 0.0;
		for(int i=0; i<funcs.size(); i++) {
			long begin = System.currentTimeMillis();
			for(int j=0; j<N; j++) {
				x += 1e-15;
				out += funcs.get(i).apply(x);
			}
			long end = System.currentTimeMillis();
			System.out.println("Time: "+((end-begin)/1000.0)+" expr="+exprs.get(i));
		}
		System.out.println("Test Value="+out);
	}

}
