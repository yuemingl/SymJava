package lambdacloud.core.utils;

public class Utils {

	public static double[] flatten(double[][] args) {
		int len = 0;
		for(int i=0; i<args.length; i++)
			len += args[i].length;
		double[] ret = new double[len];
		len = 0;
		for(int i=0; i<args.length; i++) {
			System.arraycopy(args[i], 0, ret, len, args[i].length);
			len += args[i].length;
		}
		return ret;
	}

}
