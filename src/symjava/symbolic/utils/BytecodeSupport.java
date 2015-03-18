package symjava.symbolic.utils;

public class BytecodeSupport {
	public static double powi(double base, int exp) {
		if(exp == 0) return 1.0;
		else if(exp < 0) return 1.0/powi(base, -exp);
		else if(exp == 1) return base;
		else { //exp >= 2
			double rlt = 1.0;
			double tmp = base;
			if((exp & 0x1) > 0) rlt = base;
			int mask = exp>>>1;
			while(mask > 0) {
				tmp *= tmp;
				if((mask & 0x1) > 0) {
					rlt *= tmp;
				}
				mask >>>= 1;
			}
			return rlt;
		}
	}
	
	public static double sqrt(double expr, double root) {
		return Math.pow(expr, 1.0/root);
	}
	
	public static void main(String[] args) {
		System.out.println(powi(2,-3));
		System.out.println(powi(2,0));
		System.out.println(powi(2,3));
	}
}
