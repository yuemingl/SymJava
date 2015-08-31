package symjava.examples;

import symjava.bytecode.BytecodeVecFunc;
import symjava.math.SymMath;
import symjava.symbolic.Expr;
import symjava.symbolic.Symbol;
import symjava.symbolic.Symbols;
import symjava.symbolic.utils.AddList;
import symjava.symbolic.utils.JIT;
import symjava.symbolic.utils.MulList;

//http://www.sfu.ca/~ssurjano/griewank.html
public class BenchmarkGriewank {

	public static void main(String[] args) {
		test();
	}
	
	public static void test() {
		// TODO Auto-generated method stub
		int N = 50;
		long start;
		Symbol i = new Symbol("i");
		Symbols xi = new Symbols("x", i);
		
		start = System.currentTimeMillis();
		AddList term1 = new AddList();
		for (int j = 0; j < N; j++) {
			term1.add(xi.get(j) * xi.get(j) / 4000.0);
		}
		MulList term2 = new MulList();
		for (int j = 0; j < N; j++) {
			term2.add(SymMath.cos(xi.get(j) / Math.sqrt(j + 1)));
		}

		Expr griewank = term1.toExpr() - term2.toExpr() + 1.0;
		//System.out.println(griewank);
		
		Expr[] grad = new Expr[N];
		for(int j=0;j<N;j++)
			grad[j] = griewank.diff(xi.get(j));
		long symbolTime = System.currentTimeMillis() - start;
		
		Expr[] args = xi.get(0, N);
		start = System.currentTimeMillis();
		BytecodeVecFunc f = JIT.compile(args, grad);
		long compileTime = System.currentTimeMillis() - start;
		
		double[] outAry = new double[N];
		double[] params = new double[N];
		for(int j=0; j<N; j++) {
			params[j] = 0.1;
		}
		f.apply(outAry, 0, params);
		for(int j=0;j<N;j++)
			System.out.println(outAry[j]);
		System.out.println("symbol time: "+symbolTime/1000.0);
		System.out.println("compile time: "+compileTime/1000.0);
	}

}
