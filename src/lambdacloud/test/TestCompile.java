package lambdacloud.test;

import static symjava.symbolic.Symbol.*;
import symjava.bytecode.BytecodeBatchFunc;
import symjava.symbolic.Expr;

public class TestCompile {

	/**
	 * test comiple, compileVec, compileBatch
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BytecodeBatchFunc func = CompileUtils.compileBatchFunc("test1", new Expr[]{z,y*y,x+y+z}, x,y,z);
		double[] outAry = new double[3];
		func.apply(outAry, 0, new double[]{1,2,3});
		for(double d : outAry) {
			System.out.println(d);
		}
	}

}
