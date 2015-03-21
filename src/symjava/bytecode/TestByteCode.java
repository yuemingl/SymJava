package symjava.bytecode;

import java.math.BigInteger;

public class TestByteCode {
	public static double fun(double[] args) {
//		return args[0] + args[1];
		double s = Math.pow(2, 3);
		return s;
	}
	public static void main(String[] args) {
		BigInteger b = 2;
		BigInteger c = BigInteger.valueOf(3);
		System.out.println(b<c);		
	}
}
