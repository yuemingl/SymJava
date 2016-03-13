package lambdacloud.test;

public class TestUtils {
	public static boolean assertEqual(double[] a, double[] b) {
		if(a.length != b.length) {
			System.err.println("Failed! a.length != b.length: "+a.length+" != "+b.length);
			return false;
		}
		for(int i=0; i<a.length; i++) {
			if(Math.abs(a[i]-b[i])>1e-8) {
				System.err.println("Failed! a["+i+"] != b["+i+"]: "+a[i]+" != "+b[i]);
				return false;
			}
		}
		System.out.println("Passed!");
		return true;
	}
}
