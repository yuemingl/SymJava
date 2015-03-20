package symjava.symbolic.utils;

import java.lang.reflect.Method;

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
	

	public static double log(double base, double expr) {
		return Math.log(expr)/Math.log(base);
	}

	public static double numIntegrate1D(double begin, double end, double step, String className) {

		Method method;
		try {
			method = BytecodeSupport.class.getClassLoader().
					loadClass("symjava.bytecode."+className).
					getMethod("apply", new Class[] {double[].class});
			double[] args = { 0 };
			double sum = 0.0;
//			for(double i=begin; i<=end; i+=step) {
//				args[0] = i;
//				Double val = (Double)method.invoke(null, args);
//				//Double val = test_pdf(args);
//				sum += val*step;
//			}
			
			args[0] = begin;
			Double val1 = (Double)method.invoke(null, args);
			double i = begin + step;
			for(; i<=end; i+=step) {
				args[0] = i;
				Double val2 = (Double)method.invoke(null, args);
				sum += (val1+val2)*step/2.0;
				val1 = val2;
			}
			if(i - end > 0.0) {
				args[0] = end;
				Double val2 = (Double)method.invoke(null, args);
				sum += (val1+val2)*(step-(i-end))/2.0;
			}
			return sum;
		} catch (Exception e) {
			e.printStackTrace();
		}
//		for (Method m : methods) {
//		    if (methodName.equals(m.getName())) {
//		        // for static methods we can use null as instance of class
//		        m.invoke(null, new Object[] {args});
//		        break;
//		    }
//		}
		return 0.0;
	}

	public static double test_pdf(double[] args) {
		double x = args[0];
		return Math.exp(-0.5*x*x);
		//return Math.exp(-0.5*x*x)/Math.sqrt(2*Math.PI);
	}
	
	public static void main(String[] args) {
		System.out.println(powi(2,-3));
		System.out.println(powi(2,0));
		System.out.println(powi(2,3));
		
		System.out.println(numIntegrate1D(-10,10,0.1,"integrand_ae3c65a143de42739270977b140b4fdf")/Math.sqrt(2*Math.PI));
	}
}
