package io.lambdacloud.symjava.symbolic.utils;

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

	/**
	 * 1D integration
	 * @param begin
	 * @param end
	 * @param step
	 * @param integrandFunc
	 * @param additionalParams
	 * @return
	 */
	public static double numIntegrate1D(double begin, double end, double step, 
			String integrandFunc, double[] additionalParams) {

		Method method;
		try {
			method = BytecodeSupport.class.getClassLoader().
					loadClass("symjava.bytecode."+integrandFunc).
					getMethod("apply", new Class[] {double[].class});
			//additionalParams is always not null
			double[] args = new double[additionalParams.length+1];
			System.arraycopy(additionalParams, 0, args, 1, additionalParams.length);
			double sum = 0.0;
			
//			for(double i=begin; i<=end; i+=step) {
//				args[0] = i;
//				Double val = (Double)method.invoke(null, args);
//				//Double val = test_pdf(args);
//				sum += val*step;
//			}
			
			// Use trapezoid rule 
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

	/**
	 * 1D integration used in 2D integration
	 * @param begin
	 * @param end
	 * @param step
	 * @param integrandFunc
	 * @param yParam
	 * @param additionalParams
	 * @return
	 */
	public static double numIntegrate1D(double begin, double end, double step, 
			String integrandFunc, double yParam, double[] additionalParams) {

		Method method;
		try {
			method = BytecodeSupport.class.getClassLoader().
					loadClass("symjava.bytecode."+integrandFunc).
					getMethod("apply", new Class[] {double[].class});
			//additionalParams is always not null
			double[] args = new double[additionalParams.length+2];
			System.arraycopy(additionalParams, 0, args, 2, additionalParams.length);
			args[0] = 0.0;
			args[1] = yParam;
			double sum = 0.0;
			
			// Use trapezoid rule 
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
		return 0.0;
	}
	
	public static double numIntegrate2D(double begin, double end, double step, 
			String lowerBoundFunc, String upperBoundFunc, double stepInner, 
			String integrandFunc,
			double[] additionalParams) {
		Method mLower, mUpper;
		try {
			mLower = BytecodeSupport.class.getClassLoader().
					loadClass("symjava.bytecode."+lowerBoundFunc).
					getMethod("apply", new Class[] {double[].class});
			mUpper = BytecodeSupport.class.getClassLoader().
					loadClass("symjava.bytecode."+upperBoundFunc).
					getMethod("apply", new Class[] {double[].class});
			
			//additionalParams is always not null
			double[] args = new double[additionalParams.length+1];
			System.arraycopy(additionalParams, 0, args, 1, additionalParams.length);
			double sum = 0.0;
			
			// Use trapezoid rule 
			args[0] = begin;
			Double valLower = (Double)mLower.invoke(null, args);
			Double valUpper = (Double)mUpper.invoke(null, args);
			Double val1 = numIntegrate1D(valLower, valUpper, stepInner, integrandFunc, begin, additionalParams);
			
			double i = begin + step;
			for(; i<=end; i+=step) {
				args[0] = i;
				valLower = (Double)mLower.invoke(null, args);
				valUpper = (Double)mUpper.invoke(null, args);
				Double val2 = numIntegrate1D(valLower, valUpper, stepInner, integrandFunc, i, additionalParams);
				sum += (val1+val2)*step/2.0;
				val1 = val2;
			}
			if(i - end > 0.0) {
				args[0] = end;
				valLower = (Double)mLower.invoke(null, args);
				valUpper = (Double)mUpper.invoke(null, args);
				Double val2 = numIntegrate1D(valLower, valUpper, stepInner, integrandFunc, step-(i-end), additionalParams);
				sum += (val1+val2)*(step-(i-end))/2.0;
			}
			return sum;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0.0;
	}
	
	public static double numIntegrateND(double begin, double end, double step, 
			String[] lowerBoundFunc, String[] upperBoundFunc, double[] stepInner, 
			String integrandFunc, int level) {
		if(level == 1) {
			return numIntegrate1D(begin, end, step, integrandFunc, null);
		}
		
		int idx = stepInner.length-level;
		level--;
		
		Method mLower, mUpper;
		try {
			mLower = BytecodeSupport.class.getClassLoader().
					loadClass("symjava.bytecode."+lowerBoundFunc).
					getMethod("apply", new Class[] {double[].class});
			mUpper = BytecodeSupport.class.getClassLoader().
					loadClass("symjava.bytecode."+upperBoundFunc).
					getMethod("apply", new Class[] {double[].class});
			
			double[] args = { 0 };
			double sum = 0.0;
			
			// Use trapezoid rule 
			args[0] = begin;
			Double valLower = (Double)mLower.invoke(null, args);
			Double valUpper = (Double)mUpper.invoke(null, args);
			double valStep = stepInner[idx];
			Double val1 = numIntegrateND(valLower, valUpper, valStep, 
					lowerBoundFunc, upperBoundFunc, stepInner, integrandFunc, level);
			
			double i = begin + step;
			for(; i<=end; i+=step) {
				args[0] = i;
				valLower = (Double)mLower.invoke(null, args);
				valUpper = (Double)mUpper.invoke(null, args);
				Double val2 = numIntegrateND(valLower, valUpper, valStep, 
						lowerBoundFunc, upperBoundFunc, stepInner, integrandFunc, level);
				sum += (val1+val2)*step/2.0;
				val1 = val2;
			}
			if(i - end > 0.0) {
				args[0] = end;
				valLower = (Double)mLower.invoke(null, args);
				valUpper = (Double)mUpper.invoke(null, args);
				Double val2 = numIntegrateND(valLower, valUpper, valStep, 
						lowerBoundFunc, upperBoundFunc, stepInner, integrandFunc, level);
				sum += (val1+val2)*(step-(i-end))/2.0;
			}
			return sum;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0.0;
	}
	
	public static double numIntegrateMonteCarloND(String[] minBound, String[] maxBound,
			String integrandFunc, String constraint,
			double[] additionalParams) {
		int dim = minBound.length;
		try {
			Method integrand = BytecodeSupport.class.getClassLoader().
					loadClass("symjava.bytecode."+integrandFunc).
					getMethod("apply", new Class[] {double[].class});
			double[] args = new double[dim + additionalParams.length];
			System.arraycopy(additionalParams, 0, args, dim, additionalParams.length);
			
			Method[] mLower = new Method[dim];
			Method[] mUpper = new Method[dim];
			for(int i=0; i<dim; i++) {
				mLower[i] = BytecodeSupport.class.getClassLoader().
						loadClass("symjava.bytecode."+minBound[i]).
						getMethod("apply", new Class[] {double[].class});
				mUpper[i] = BytecodeSupport.class.getClassLoader().
						loadClass("symjava.bytecode."+maxBound[i]).
						getMethod("apply", new Class[] {double[].class});
			}
			double[] minBd = new double[dim];
			double[] maxBd = new double[dim];
			Double val;
			for(int i=0; i<dim; i++) {
				val = (Double)mLower[i].invoke(null, args);
				minBd[i] = val;
				val = (Double)mUpper[i].invoke(null, args);
				maxBd[i] = val;
			}
			Method constr = BytecodeSupport.class.getClassLoader().
					loadClass("symjava.bytecode."+constraint).
					getMethod("apply", new Class[] {double[].class});
			double cubeVol = 1.0;
			for(int i=0; i<dim; i++) {
				cubeVol *= (maxBd[i] -minBd[i]);
			}
			
			//Numerical intensive loop
			double sum = 0.0;
			int inAreaPointCount = 0;
			int NN = 0;
			double result = Double.MAX_VALUE;
			double eps = 1e-8;
			while(true) {
				NN++;
				for(int i=0; i<dim; i++) {
					args[i] = minBd[i] + Math.random()*(maxBd[i] -minBd[i]);
				}
				
				Double flag = (Double)constr.invoke(null, args);
				if(flag > 0.5) { //The random point is in the domain
					sum += (Double)integrand.invoke(null, args);
					inAreaPointCount++;
				}
				//System.out.println("inAreaPointCount="+inAreaPointCount);
				//System.out.println("cubeVol="+cubeVol);
				//System.out.println("area="+area);
				if(inAreaPointCount % 10000 == 1) {
					double area = cubeVol*inAreaPointCount/(double)NN;
					double curResult = (sum/inAreaPointCount)*area;
					if(Math.abs(result - curResult) < eps) {
						System.out.println("Monte Carlo evaluation times="+NN);
						return curResult;
					}
					result = curResult;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		
		System.out.println(numIntegrate1D(-10,10,0.1,"integrand_ae3c65a143de42739270977b140b4fdf", null)/Math.sqrt(2*Math.PI));
	}
}

 