package lambdacloud.test;

import symjava.symbolic.Matrix;

public class TestMatrix {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Matrix m = new Matrix("m",3,3);
		CompileUtils.compile("test", m, m);
	}

}
