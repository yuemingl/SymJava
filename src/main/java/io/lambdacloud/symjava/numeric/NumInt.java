package io.lambdacloud.symjava.numeric;

import io.lambdacloud.symjava.bytecode.BytecodeFunc;
import io.lambdacloud.symjava.symbolic.Func;
import io.lambdacloud.symjava.symbolic.Integrate;

/**
 * Numerical Integration
 * 
 */
public class NumInt {
	Integrate integrate;
	BytecodeFunc byteFunc;

	public NumInt(Integrate integrate) {
		this.integrate = integrate;
		if(this.integrate.integrand instanceof Func) {
			Func f = (Func) this.integrate.integrand;
			byteFunc = f.toBytecodeFunc();
		} else {
			Func f = new Func(
					this.getClass().getSimpleName()+java.util.UUID.randomUUID().toString().replaceAll("-", ""),
					this.integrate.integrand);
			byteFunc = f.toBytecodeFunc();
		}
	}
	
	public double eval() {
		double[][] pnts = integrate.domain.getIntWeightAndPoints(3);
		int dim = integrate.domain.getDim();
		double sum = 0.0;
		for(int k=0; k<pnts.length; k++) {
			sum += byteFunc.apply(pnts[k])*pnts[k][dim];
		}
		return sum;
	}
	
	public double eval(double ...params) {
		double[][] pnts = integrate.domain.getIntWeightAndPoints(3);
		int dim = integrate.domain.getDim();
		double[] allArgs = new double[dim + params.length];
		double sum = 0.0;
		for(int k=0; k<pnts.length; k++) {
			for(int i=0; i<dim; i++) {
				allArgs[i] = pnts[k][i];
			}
			for(int i=dim; i<allArgs.length; i++) {
				allArgs[i] = params[i-dim];
			}
			sum += byteFunc.apply(allArgs)*pnts[k][dim];
		}
		return sum;
	}
}
