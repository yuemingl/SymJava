package io.lambdacloud.test;

import static io.lambdacloud.symjava.math.SymMath.abs;
import static io.lambdacloud.symjava.math.SymMath.sin;

import java.util.HashMap;
import java.util.Map;

import io.lambdacloud.core.CloudSD;
import io.lambdacloud.core.Session;
import io.lambdacloud.symjava.symbolic.Matrix;

public class TestMathFunction {

	public static void main(String[] args) {
		Matrix A = new Matrix("A",4,4);
		Map<String, double[]> dict = new HashMap<String, double[]>();
		/*
		-1 2 3 4
		1 2 1 3
		1 2 2 1
		2 3 1 4
		*/
		//matrix stored in cloumnwise
		dict.put(A.toString(), new double[]{-1,1,1,2,2,2,2,3,3,1,2,1,4,3,1,4});
		
		Session sess1 = new Session();
		
		CloudSD rlt1 = sess1.runVec(A, dict);
		rlt1.fetch();
		for(double d : rlt1.getData())
			System.out.println(d);
		
		CloudSD rlt2 = sess1.runVec(sin(A), dict);
		rlt2.fetch();
		for(double d : rlt2.getData())
			System.out.println(d);
		
		CloudSD rlt3 = sess1.runVec(abs(A), dict);
		rlt3.fetch();
		for(double d : rlt3.getData())
			System.out.println(d);
	}

}
