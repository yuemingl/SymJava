package symjava.examples;

import symjava.relational.Le;
import symjava.symbolic.*;
import static symjava.math.SymMath.*;
import static symjava.symbolic.Symbol.*;
import symjava.bytecode.BytecodeFunc;
import symjava.domains.Domain;
import symjava.domains.Domain2D;
import symjava.domains.Interval;
import symjava.symbolic.utils.JIT;

public class NumericalIntegration {

	public static void main(String[] args) {
		test_1D();
		//test_2D();
	}
	
	public static void test_1D() {
		//Define the interal
		Domain I = Interval.apply(-10, 0).setStepSize(0.01);
		//Define the integral: cumulative distribution function for standard normal distribution
		Expr cdf = Integrate.apply(exp(-0.5*pow(x,2))/sqrt(2*PI), I);
		System.out.println(cdf); //\int_{-10.0}^{10.0}{1/\sqrt{2*\pi}*e^{-0.5*x^2}}dx
		//Compile cdf to perform numerical integration
		BytecodeFunc f = JIT.compile(cdf);
		System.out.println(f.apply()); //0.5
		
		//Define the integral: cumulative distribution function for a general normal distribution
		Symbol mu = new Symbol("\\mu");
		Symbol sigma = new Symbol("\\sigma");
		Expr cdf2 = Integrate.apply(exp(-pow(x-mu,2)/(2*sigma*sigma))/(sigma*sqrt(2*PI)), I);
		System.out.println(cdf2); //\int_{-10.0}^{0.0}{1/(\sigma*\sqrt{2*\pi})*e^{-(-\mu + x)^2/(2*\sigma*\sigma)}}dx
		//Compile cdf2 to perform numerical integration
		BytecodeFunc f2 = JIT.compile(new Expr[]{mu, sigma}, cdf2);
		System.out.println(f2.apply(-5.0, 1.0)); //~1.0
		
	}
	
	public static void test_2D() {
		//http://turing.une.edu.au/~amth142/Lectures/Lecture_14.pdf
		/*
		Example:
		We will evaluate the integral
			I = \int_{\Omega} sin(sqrt(log(x+y+1))) dxdy
		where \Omega is the disk
			(x-1/2)^2 + (y-1/2)^2 <= 1/4
		Since the disk Ω is contained within the square [0, 1] × [0, 1], we can
		generate x and y as uniform [0, 1] random numbers, and keep those which
		lie in the disk Ω.
		
		Matlab code:
		function ii = monte2da(n)
			k = 0 // count no. of points in disk
			sumf = 0 // keep running sum of function values
			while (k < n) // keep going until we get n points
				x = rand(1,1)
				y = rand(1,1)
				if ((x-0.5)^2 + (y-0.5)^2 <= 0.25) then // (x,y) is in disk
					k = k + 1 // increment count
					sumf = sumf + sin(sqrt(log(x+y+1))) // increment sumf
				end
			end
			ii = (%pi/4)*(sumf/n) // %pi/4 = volume of disk
		endfunction
		-->monte2da(100000)
		ans =
		0.5679196
		*/
//		Domain omega = new Domain2D("\\Omega", x, y)
//				.setConstraint( Le.apply((x-0.5)*(x-0.5) + (y-0.5)*(y-0.5), C(1/4)) )
//				.setBound(x, Cm1, C1)
//				.setBound(y, Cm1, C1);
		Domain omega = new Domain2D("\\Omega", x, y)
		//.setConstraint( Le.apply((x-0.5)*(x-0.5) + (y-0.5)*(y-0.5), C(1/4)) )
		.setBound(x, Cm1, C1) 
		.setBound(y, Cm1, C1) //TODO How to deal with parameters? e.g. setBound(x, a, b); call f.apply(-1,1)
		.setStepSize(0.001);
		
		Expr ii = Integrate.apply(sin(x*x+y*y), omega);
		System.out.println(ii);
		BytecodeFunc f = JIT.compile(ii);
		System.out.println(f.apply());
		test_2D_verifiy();
		
		
	}
	
	public static void test_2D_verifiy() {
		double xMin=-1, xMax=1, xStep=0.0001;
		double yMin=-1, yMax=1, yStep=0.0001;
		double sum = 0.0;
		for(double x=xMin; x<=xMax; x+=xStep) {
			for(double y=yMin; y<=yMax; y+=yStep) {
				sum += Math.sin(x*x+y*y)*xStep*yStep;
			}
		}
		System.out.println("verify="+sum);
	}

}



